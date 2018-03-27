package com.yiban.erp.dto;

public class VerifyCodeReq {
    private String mobile;
    private String bizNo;
    private String userName;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
