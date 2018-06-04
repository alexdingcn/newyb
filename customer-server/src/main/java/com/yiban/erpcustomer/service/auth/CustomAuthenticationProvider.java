package com.yiban.erpcustomer.service.auth;

import com.yiban.erpcustomer.constant.IdentifierType;
import com.yiban.erpcustomer.constant.OperationLogType;
import com.yiban.erpcustomer.dao.OperationLogMapper;
import com.yiban.erpcustomer.dao.UserAuthMapper;
import com.yiban.erpcustomer.dao.UserMapper;
import com.yiban.erpcustomer.entities.OperationLog;
import com.yiban.erpcustomer.entities.User;
import com.yiban.erpcustomer.entities.UserAuth;
import com.yiban.erpcustomer.exception.BizRuntimeException;
import com.yiban.erpcustomer.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private UserMapper userMapper;
    private UserAuthMapper userAuthMapper;
    private OperationLogMapper operationLogMapper;

    @Inject
    public CustomAuthenticationProvider(UserMapper userMapper, UserAuthMapper userAuthMapper, OperationLogMapper operationLogMapper) {
        this.userMapper = userMapper;
        this.userAuthMapper = userAuthMapper;
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String username = authentication.getName();
        List<String> credentials = (List<String>) authentication.getCredentials();
        if (StringUtils.isEmpty(username)) {
            logger.warn("username missing");
            throw new BizRuntimeException(ErrorCode.LOGIN_USERNAME_MISSING);
        }
        if (credentials == null || credentials.size() != 2) {
            logger.warn("Missing credentials");
            throw new BizRuntimeException(ErrorCode.LOGIN_PASSWORD_INVALID);
        }
        String password = credentials.get(0);
        if (StringUtils.isEmpty(password)) {
            logger.warn("password missing");
            throw new BizRuntimeException(ErrorCode.LOGIN_PASSWORD_INVALID);
        }
        // identifier could by username/mobile/qq/weixin/weibo etc...
        User user = userMapper.findUserByNameOrMobile(username);
        if (user == null) {
            logger.warn("username/mobile = " + username + " not found");
            throw new BizRuntimeException(ErrorCode.USER_NAME_NOT_EXISTED);
        }

        // 认证逻辑
        if (new BCryptPasswordEncoder().matches(password, user.getCredential())) {
            logger.info("Login in successfully, username={}", username);
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

            saveLoginLog(user);

            // save wx openid
            saveWxOpenid(credentials.get(1), user);


            Authentication auth = new UsernamePasswordAuthenticationToken(user.getCompactUser(), password, grantedAuthorities);
            return auth;
        } else {
            logger.warn("Login password not match, username={}", username);
            throw new BizRuntimeException(ErrorCode.LOGIN_PASSWORD_INVALID);
        }
    }

    private void saveWxOpenid(String openId, User user) {
        if (StringUtils.isNotEmpty(openId)) {
            UserAuth userAuth = userAuthMapper.findByIdentifier(openId, IdentifierType.WEIXIN.name());
            if (userAuth == null) {
                userAuth = new UserAuth();
                userAuth.setUserId(user.getId());
                userAuth.setIdentifierType(IdentifierType.WEIXIN.name());
                userAuth.setIdentifier(openId);
                userAuth.setVerified(true);
                userAuth.setCreatedBy(String.valueOf(user.getId()));
                userAuth.setCreatedTime(new Date());
                userAuthMapper.insert(userAuth);
            }
        }

    }

    private void saveLoginLog(User user) {
        OperationLog operationLog = operationLogMapper.getLastLogin(user.getId());
        if (operationLog != null) {
            user.setLastLoginTime(operationLog.getCreatedTime());
        }

        operationLog = new OperationLog();
        operationLog.setUserId(user.getId());
        operationLog.setType(OperationLogType.LOGIN.name());
        operationLog.setCreatedTime(new Date());
        operationLog.setCreatedBy(user.getId().toString());
        operationLogMapper.insert(operationLog);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
