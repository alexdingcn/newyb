package com.yiban.erpcustomer.service.auth;

import com.yiban.erpcustomer.constant.OperationLogType;
import com.yiban.erpcustomer.dao.OperationLogMapper;
import com.yiban.erpcustomer.dao.UserMapper;
import com.yiban.erpcustomer.entities.OperationLog;
import com.yiban.erpcustomer.entities.User;
import com.yiban.erpcustomer.exception.BizRuntimeException;
import com.yiban.erpcustomer.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private UserMapper userMapper;
    private OperationLogMapper operationLogMapper;

    @Inject
    public CustomAuthenticationProvider(UserMapper userMapper, OperationLogMapper operationLogMapper) {
        this.userMapper = userMapper;
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (StringUtils.isEmpty(username)) {
            logger.warn("username missing");
            throw new BizRuntimeException(ErrorCode.LOGIN_USERNAME_MISSING);
        }
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

            Authentication auth = new UsernamePasswordAuthenticationToken(user.getCompactUser(), password, grantedAuthorities);
            return auth;
        } else {
            logger.warn("Login password not match, username={}", username);
            throw new BizRuntimeException(ErrorCode.LOGIN_PASSWORD_INVALID);
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
