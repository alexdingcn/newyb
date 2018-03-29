package com.yiban.erp.controller.auth;


import com.yiban.erp.constant.IdentifierType;
import com.yiban.erp.dao.UserAuthMapper;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserAuth;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAuthMapper userAuthMapper;

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody Map requestMap) {
        String username = (String) requestMap.getOrDefault("userName", "");
        String password = (String) requestMap.getOrDefault("password", "");
        String mobile = (String) requestMap.getOrDefault("mobile", "");

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(mobile)) {
            User user = new User();
            user.setNickname(username);
            user.setCreatedBy("admin");
            user.setCreatedTime(new Date());
            user.setMobile(mobile);
            try {
                if (userMapper.insert(user) > 0) {
                    String encryptedPass = new BCryptPasswordEncoder().encode(password);

                    UserAuth userAuth = new UserAuth();
                    userAuth.setUserId(user.getId());
                    userAuth.setIdentifierType(IdentifierType.USERNAME.name());
                    userAuth.setIdentifier(username);
                    userAuth.setCredential(encryptedPass);
                    userAuth.setVerified(true);
                    userAuth.setCreatedBy(user.getCreatedBy());
                    userAuth.setCreatedTime(new Date());
                    int usernameResult = userAuthMapper.insert(userAuth);

                    userAuth = new UserAuth();
                    userAuth.setUserId(user.getId());
                    userAuth.setIdentifierType(IdentifierType.MOBILE.name());
                    userAuth.setIdentifier(mobile);
                    userAuth.setCredential(encryptedPass);
                    userAuth.setVerified(true);
                    userAuth.setCreatedBy(user.getCreatedBy());
                    userAuth.setCreatedTime(new Date());
                    int mobileResult = userAuthMapper.insert(userAuth);

                    if (usernameResult > 0 && mobileResult > 0) {
                        return ResponseEntity.ok().build();
                    }
                }
            } catch (Exception ex) {
                logger.error("Failed to create user, {}", ex.getMessage());
            }
        }

        return ResponseEntity.badRequest().body(ErrorCode.USER_REGISTER_FAIL.toString());
    }
}
