package com.yiban.erp.entities;

import java.io.Serializable;
import java.util.Date;

public class VerifyCode implements Serializable {
    private String code;
    private Date expiredTime;
    private String mobile;
    private Integer count;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getCount() {
        return count != null ? count : 0;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
