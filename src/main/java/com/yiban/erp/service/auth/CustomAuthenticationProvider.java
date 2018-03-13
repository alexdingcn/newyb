package com.yiban.erp.service.auth;

import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.dao.UserRoleMapper;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserRole;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private UserMapper userMapper;
    private UserRoleMapper userRoleMapper;

    @Inject
    public CustomAuthenticationProvider(UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (StringUtils.isEmpty(username)) {
            logger.warn("username missing");
            throw new InvalidParameterException(ErrorCode.LOGIN_USERNAME_MISSING.getMessage());
        }

        if (StringUtils.isEmpty(password)) {
            logger.warn("password missing");
            throw new InvalidParameterException(ErrorCode.LOGIN_PASSWORD_INVALID.getMessage());
        }

        // identifier could by username/mobile/qq/weixin/weibo etc...
        User user = userMapper.findUserByNameOrMobile(username);

        if (user == null) {
            logger.warn("username/mobile = " + username + " not found");
            throw new BadCredentialsException(ErrorCode.USER_NAME_NOT_EXISTED.getMessage());
        }

        // 认证逻辑
        if (new BCryptPasswordEncoder().matches(password, user.getCredential())) {
            logger.info("Login in successfully, username={}", username);
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            List<UserRole> userRoles = userRoleMapper.findByUserId(user.getId());
            if (userRoles != null) {
                for (UserRole role : userRoles) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
                }
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(user.getCompactUser(), password, grantedAuthorities);
            return auth;

        } else {
            logger.warn("Login password not match, username={}", username);
            throw new BadCredentialsException(ErrorCode.LOGIN_PASSWORD_INVALID.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
