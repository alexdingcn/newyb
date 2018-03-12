package com.yiban.erp.service.auth;

import com.yiban.erp.dao.UserAuthMapper;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.dao.UserRoleMapper;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserRole;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // identifier could by username/mobile/qq/weixin/weibo etc...
        User user = userMapper.findUserByNameOrMobile(username);

        if (user == null) {
            logger.warn("username/mobile = " + username + " not found");
            throw new BadCredentialsException(ErrorCode.USER_NAME_NOT_EXISTED.getMessage());
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        List<UserRole> userRoles = userRoleMapper.findByUserId(user.getId());
        if (userRoles != null) {
            for (UserRole role : userRoles){
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            }
        }

        return new org.springframework.security.core.userdetails.User(user.getNickname(), user.getCredential(), grantedAuthorities);
    }
}