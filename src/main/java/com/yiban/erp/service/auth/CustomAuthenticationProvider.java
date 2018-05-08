package com.yiban.erp.service.auth;

import com.yiban.erp.constant.OperationLogType;
import com.yiban.erp.constant.UserStatus;
import com.yiban.erp.dao.CompanyMapper;
import com.yiban.erp.dao.OperationLogMapper;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.dao.UserRouteMapper;
import com.yiban.erp.entities.Company;
import com.yiban.erp.entities.OperationLog;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserRoute;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private UserMapper userMapper;
    private CompanyMapper companyMapper;
    private UserRouteMapper userRouteMapper;
    private OperationLogMapper operationLogMapper;

    @Inject
    public CustomAuthenticationProvider(UserMapper userMapper, UserRouteMapper userRouteMapper,
                                        CompanyMapper companyMapper, OperationLogMapper operationLogMapper) {
        this.userMapper = userMapper;
        this.userRouteMapper = userRouteMapper;
        this.companyMapper = companyMapper;
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
        // 验证用户名对应的公司是否处于可用状态和用户的状态是否可用
        Company company = companyMapper.selectByPrimaryKey(user.getCompanyId());
        if (company == null || company.getEnabled() == null || !company.getEnabled()) {
            logger.warn("user company is null or enabled status is false. userId:{} companyId:{}", user.getId(), user.getCompanyId());
            throw new BizRuntimeException(ErrorCode.COMPANY_DISABLED);
        }
        if (company.getExpiredTime() != null) {
            if (company.getExpiredTime().before(new Date())) {
                logger.warn("Trial is expired. userId:{} companyId:{}", user.getId(), user.getCompanyId());
                throw new BizRuntimeException(ErrorCode.COMPANY_EXPIRED);
            } else {
                user.setCompanyExpiredTime(company.getExpiredTime());
            }
        }
        if (UserStatus.NORMAL.getCode() != user.getStatus()) {
            logger.warn("user status is not in normal status. userId:{}, status:{}", user.getId(), user.getStatus());
            throw new BizRuntimeException(ErrorCode.USER_LOGIN_UN_ACTIVATE);
        }

        // 认证逻辑
        if (new BCryptPasswordEncoder().matches(password, user.getCredential())) {
            logger.info("Login in successfully, username={}", username);
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            List<UserRoute> userRoutes = userRouteMapper.getByUserId(user.getId());
            if (userRoutes != null) {
                for (UserRoute route : userRoutes) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(route.getRouteName()));
                }
            }

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
