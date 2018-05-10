package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 往来账的类型
 */
public enum FinancialBizType {

    BUY_IN("采购入库", false),
    BUY_BACK("采购退货", false),
    SELL_BACK("销售退货", false),
    SELL_BACK_FREE("销售退货免零", false),
    SELL_BATCH("批发销售", false),

    RECEIVE("收款", true),
    PAY("付款", true),
    PRE_PAID("预付款", true),
    PRE_RECEIVE("预收款", true),
    RECORD_RECEIVE("记账应收", true),
    RECORD_PAY("记账应付", true),

    OFFSET("冲销", false),

    RECEIVE_CANCEL("收款取消", false),
    PAY_CANCEL("付款取消", false),
    PRE_PAID_CANCEL("预付款取消", false),
    PRE_RECEIVE_CANCEL("预收款取消", false),
    RECORD_RECEIVE_CANCEL("记账应收取消", false),
    RECORD_PAID_CANCEL("记账应付取消", false),
    ;

    private String desc;
    private boolean canCancel;

    FinancialBizType(String desc, boolean canCancel) {
        this.desc = desc;
        this.canCancel = canCancel;
    }

    public static FinancialBizType getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (FinancialBizType type : FinancialBizType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据关联单，获取对应的取消操作类型
     * @param type
     * @return
     */
    public static FinancialBizType getCancelBizTypeByRefBizType(FinancialBizType type) {
        switch (type) {
            case RECEIVE:
                return RECEIVE_CANCEL;
            case PAY:
                return PAY_CANCEL;
            case PRE_PAID:
                return PRE_PAID_CANCEL;
            case PRE_RECEIVE:
                return PRE_RECEIVE_CANCEL;
            case RECORD_RECEIVE:
                return RECORD_RECEIVE_CANCEL;
            case RECORD_PAY:
                return RECORD_PAID_CANCEL;
            default:
                return null;
        }
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }
}
