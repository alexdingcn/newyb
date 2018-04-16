package com.yiban.erp.constant;

public enum OptionsType {
    SHIP_METHOD("运输方式"),
    SHIP_TOOL("运输工具"),
    TEMPER_CONTROL("温控方式"),
    TEMPER_STATUS("温控状况"),
    PAY_METHOD("收款方式"),
    SELL_ORDER_REVIEW("销售订单审核方式"),
    BUY_TYPE("采购属性"),
    BILL_TYPE("发票种类"),
    WAREHOUSE_IN_TYPE("入库类型"),
    WAREHOUSE_OUT_TYPE("入库类型"),
    WAREHOUSE_CHECK_TYPE("盘点类型"),
    WAREHOUSE_CHECK_STATE("盘点单状态"),
    CHECK_PLAN_NORMAL("正常"),
    CHECK_PLAN_LOSE("盘亏"),
    CHECK_PLAN_MORE("盘盈"); ;

    private String description;

    OptionsType(String desc) {
        this.description = desc;
    }
}
