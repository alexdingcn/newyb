package com.yiban.erp.constant;

public enum OrderNumberType {

    CUST("CU", "客户编号"),
    GOODS("SP", "商品编号"),
    SELL("SO", "销售"),
    BUY("BO", "采购"),
    BUY_BACK("BB", "采购退货"),
    BACK_R("BR", "退库"),
    SELL_BACK("SG", "销售退货"),
    DAMAGED("DA", "报损"),
    INVENTORY("IY", "盘点"),
    MOVE_R("MR", "移库"),
    IN_CHECK("IC", "入库质检"),
    FINANCIAL_FLOW("AC", "往来账流水号"),
    ;

    private String code;
    private String desc;

    OrderNumberType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
