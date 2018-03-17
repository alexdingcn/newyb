package com.yiban.erp.constants;

public enum OptionsType {
    SHIP_METHOD("运输方式"),
    SHIP_TOOL("运输工具"),
    TEMPER_CONTROL("温控方式"),
    PAY_METHOD("收款方式"),
    ;

    private String description;

    OptionsType(String desc) {
        this.description = desc;
    }
}
