package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 入库单来源类型
 */
public enum RepertoryRefType {

    BUY_ORDER("采购入库"),
    ;

    private String desc;

    RepertoryRefType(String desc) {
        this.desc = desc;
    }

    public static RepertoryRefType getByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (RepertoryRefType type : RepertoryRefType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
