package com.yiban.erp.dto;

import java.math.BigDecimal;

public class CustomerCategoryPrice {

    private Integer customerCategoryId;
    private String customerCategoryName;
    private BigDecimal batchDiscount;
    private BigDecimal retailDiscount;
    private BigDecimal batchPrice;
    private BigDecimal retailPrice;

    public Integer getCustomerCategoryId() {
        return customerCategoryId;
    }

    public void setCustomerCategoryId(Integer customerCategoryId) {
        this.customerCategoryId = customerCategoryId;
    }

    public String getCustomerCategoryName() {
        return customerCategoryName;
    }

    public void setCustomerCategoryName(String customerCategoryName) {
        this.customerCategoryName = customerCategoryName;
    }

    public BigDecimal getBatchDiscount() {
        return batchDiscount;
    }

    public void setBatchDiscount(BigDecimal batchDiscount) {
        this.batchDiscount = batchDiscount;
    }

    public BigDecimal getRetailDiscount() {
        return retailDiscount;
    }

    public void setRetailDiscount(BigDecimal retailDiscount) {
        this.retailDiscount = retailDiscount;
    }

    public BigDecimal getBatchPrice() {
        return batchPrice;
    }

    public void setBatchPrice(BigDecimal batchPrice) {
        this.batchPrice = batchPrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }
}
