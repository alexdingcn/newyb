package com.yiban.erp.constant;

public enum UserStatus {

    NORMAL(1, "正常"),
    UNACTIVITY(0, "未激活"),
    CANCEL(-1, "禁用");

    private Integer code;
    private String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
