package com.yiban.erp.constant;

public enum SellReviewType {

    QUALITY_REVIEW("出库质量审核"),
    SALE_REVIEW("销售审核");

    private String desc;

    SellReviewType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
