package com.yiban.erpcustomer.exception;

public enum ErrorDisplay {

    MESSAGE(1),
    NOTICE(2),
    MODAL(3);

    private Integer code;

    ErrorDisplay(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
