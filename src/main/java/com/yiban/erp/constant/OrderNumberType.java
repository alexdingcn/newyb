package com.yiban.erp.constant;

public enum OrderNumberType {

    CUST("CU", "客户编号"),
    SELL("SO", "销售"),
    BUY("BO", "采购"),
    BACK_R("BR", "退库"),
    BACK_G("BG", "退货"),
    IN_R("IR", "入库"),
    OUT_R("OR", "出库"),
    DAMAGED("DA", "报损"),
    INVENTORY("IY", "盘点"),
    MOVE_R("MR", "移库"),
    IN_CHECK("IC", "入库质检")
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
