package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 入库单来源类型
 */
public enum RepertoryRefType {

    BUY_ORDER("采购入库"),
    BUY_DIRECT("直调入库"), //直接录入入库单，没有关联到对应的采购单
    BUY_BACK("采购退货"),
    SELL_BATCH("批发出库"),
    SELL_BACK("批发退货"),
    CHECK_LOSE("盘亏出库"),
    CHECK_SURPLUS("盘盈入库"),
    MOVE_OUT("转库出库"),
    MOVE_IN("转库入库"),
    DIRECT_IN("直调入库"),
    DIRECT_OUT("直调出库"),
    DAMAGE_OUT("破损出库"),
    DESTORY_OUT("销毁出库")/** add by wk   增加销毁类型*/
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
