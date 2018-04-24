package com.yiban.erp.constant;

/**
 * 往来账的类型
 */
public enum FinancialBizType {

    BUY_IN("采购入库"),
    BUY_OUT("采购退货"),
    BUY_PRE_PAID("采购预付款"),
    BUY_PAID("采购付款"),
    BUY_OFFSET("采购付款冲销"),

    SELL_BACK("销售退货"),
    SELL_BATCH("批发销售"),
    SELL_PRE_PAID("销售预收款"),
    SELL_PAID("销售收款"),
    SELL_OFFSET("销售收款冲销"),

    RECEIVE("收款"),
    PAY("付款"),

    RECORD_RECEIVE("记账应收"),
    RECORD_PAY("记账应付"),

    RECEIVE_CANCEL("收款冲红"),
    PAY_CANCEL("付款冲红"),
    RECORD_RECEIVE_CANCEL("记账应收冲红"),
    RECORD_BUY_CANCEL("记账应付冲红"),
    ;

    private String desc;

    FinancialBizType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
