package com.yiban.erpcustomer.entities;

import com.alibaba.fastjson.annotation.JSONField;
import com.yiban.erpcustomer.constant.IdentifierType;

import java.util.Date;

public class User {
    private Long id;

    private Integer companyId;

    private String companyName;

    private String type;

    private String nickname;

    private String mobile;

    private String realname;

    private String email;

    private String phone;

    private String idcard;

    private String forgetToken;

    private String address;

    private Date birthday;

    private Integer sex;

    private Integer status;

    private Boolean superUser; //是否为超级管理员，只有通过公司注册的才是true,其他全是false

    private String comment;

    private String avatarUrl;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private String identifier;

    private IdentifierType identifierType;

    private String credential;

    private Date lastLoginTime;
    // 公司过期时间
    private Date companyExpiredTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard == null ? null : idcard.trim();
    }

    public String getForgetToken() {
        return forgetToken;
    }

    public void setForgetToken(String forgetToken) {
        this.forgetToken = forgetToken == null ? null : forgetToken.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getSuperUser() {
        return superUser;
    }

    public void setSuperUser(Boolean superUser) {
        this.superUser = superUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy == null ? null : updatedBy.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(IdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    @JSONField(serialize = false)
    public User getCompactUser() {
        User user = new User();
        user.setMobile(this.getMobile());
        user.setCompanyId(this.getCompanyId());
        user.setCompanyName(this.getCompanyName());
        user.setNickname(this.getNickname());
        user.setRealname(this.getRealname());
        user.setCreatedTime(this.getCreatedTime());
        user.setUpdatedTime(this.getUpdatedTime());
        user.setEmail(this.getEmail());
        user.setPhone(this.getPhone());
        user.setAddress(this.getAddress());
        user.setId(this.getId());
        user.setIdcard(this.getIdcard());
        user.setStatus(this.getStatus());
        user.setLastLoginTime(this.getLastLoginTime());
        user.setCompanyExpiredTime(this.getCompanyExpiredTime());
        user.setAvatarUrl(this.getAvatarUrl());
        return user;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getCompanyExpiredTime() {
        return companyExpiredTime;
    }

    public void setCompanyExpiredTime(Date companyExpiredTime) {
        this.companyExpiredTime = companyExpiredTime;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}