package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

public enum OptionsType {
    SHIP_METHOD("运输方式"),
    SHIP_TOOL("运输工具"),
    TEMPER_CONTROL("温控方式"),
    TEMPER_STATUS("温控状况"),
    PAY_METHOD("收/付款方式"),
    SELL_ORDER_REVIEW("销售订单审核方式"),
    BUY_TYPE("采购属性"),
    BILL_TYPE("发票种类"),
    WAREHOUSE_IN_TYPE("入库类型"),
    WAREHOUSE_OUT_TYPE("入库类型"),
    WAREHOUSE_CHECK_TYPE("盘点类型"),
    WAREHOUSE_CHECK_STATE("盘点单状态"),

    BILLING_METHOD("结算方式"),
    SUPPLIER_TYPE("供应商类型"),

    STORAGE_METHOD("存储方式"),
    SPECIAL_MED("特殊管理属性"),
    GOODS_UNIT("商品计量单位"),
    PERSCRIPTION("处方/非处方属性"),
    GOODS_MED_TYPE("中/西药属性"),
    GOODS_JX("剂型属性"),
    GOODS_BASE_MED("药基属性"),
    GOODS_FUNC_CAT("功能分类属性"),
    GOODS_MEDICATION("给药途径属性"),
    GOODS_CARE_TIME("养护标志属性"),
    GOODS_GMP_TYPE("GMP认证属性"),
    GOODS_ABC_TYPE("ABC属性"),
    GOODS_SCOPE("经营范围属性"),
    GOODS_NEW_TYPE("新特药属性"),


    CHECK_PLAN_NORMAL("正常"),
    CHECK_PLAN_LOSE("盘亏"),
    CHECK_PLAN_MORE("盘盈");


    private String description;

    OptionsType(String desc) {
        this.description = desc;
    }

    public static OptionsType getByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (OptionsType type : OptionsType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
