package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

public enum ConfigKey {

    // --------- 商品相关 -------//
    /**
     * 公司业务类型，控制一些特殊型字段的显示，如药品类型的特殊监管药物等
     */
    COMPANY_TYPE("medicine", "others"),


    // --------- 订单相关 -------//
    // ---订单流程
    BUY_CHECK("open", "close"),  //采购单审核
    BUY_QUALITY_CHECK("open", "close"), //入库质量验收
    BUY_FINAL_CHECK("open", "close"),  // 入库终审
    BUY_BACK_BM_CHECK("open", "close"),  // 采购退回单采购经理审核
    BUY_BACK_QA_CHECK("open", "close"),  // 采购退回单质管经理审核
    BUY_BACK_QUALITY_CHECK("open", "close"), // 退回质量审核

    SALE_CHECK("open", "close"), //销售出库质量审核
    SALE_BACK_SM_CHECK("open", "close"), // 销售退回单销售经理审核
    SALE_BACK_QA_CHECK("open", "close"), // 销售退回单质管经理审核
    SALE_BACK_QUALITY_CHECK("open", "close"), // 销售退回质量审核
    SALE_BACK_FINAL_CHECK("open", "close"), // 销售退回终审

    SALE_PRICE("open", "close"), //销售制单是否启用特批价修改

    ;

    private String[] keyValues;  //可以列下主要的内容，也可以不存在，value不做验证

    ConfigKey(String... keyValues) {
        this.keyValues = keyValues;
    }

    public static ConfigKey getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (ConfigKey key : ConfigKey.values()) {
            if (key.name().equalsIgnoreCase(name)) {
                return key;
            }
        }
        return null;
    }

    public String[] getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(String[] keyValues) {
        this.keyValues = keyValues;
    }
}
