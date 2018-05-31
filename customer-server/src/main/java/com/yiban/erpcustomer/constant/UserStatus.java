package com.yiban.erpcustomer.constant;

public enum UserStatus {

    NORMAL(1, "正常"),
    UN_ACTIVATE(0, "未激活"),
    DELETE(-1, "删除"); // 使用与离职状态，在操作时会修改对应的用户的唯一标识(用户名和手机号)

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
