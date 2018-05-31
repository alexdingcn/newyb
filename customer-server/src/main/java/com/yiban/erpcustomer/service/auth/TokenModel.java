package com.yiban.erpcustomer.service.auth;

import com.yiban.erpcustomer.entities.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TokenModel implements Serializable {

    private static final long serialVersionUID = 1314510553248175694L;

    private Long userId;

    private User user;

    private List<String> accessApiList; //可访问的API列表(备用), 目前使用的是也到页面基本的权限显示

    private String token;

    private Date expiredTime; //过期时间，可以用于判断是否延长过期时间

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getAccessApiList() {
        return accessApiList;
    }

    public void setAccessApiList(List<String> accessApiList) {
        this.accessApiList = accessApiList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }
}
