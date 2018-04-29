package com.yiban.erp.constant;

public enum FinancialPreStatus {

    INIT("未使用"),
    OFFSET("已冲销"),
    CANCEL("已取消");

    private String desc;

    FinancialPreStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
