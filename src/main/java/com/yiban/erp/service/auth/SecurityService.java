package com.yiban.erp.service.auth;

import com.yiban.erp.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {

    UserDetails getLoggedInUser();

    void checkLogin(String identifier, String password);
}
