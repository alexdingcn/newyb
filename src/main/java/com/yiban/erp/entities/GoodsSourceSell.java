package com.yiban.erp.entities;

import java.math.BigDecimal;

/**
 * 商品溯源销售记录实体类
 * @author  wk
 */
public class GoodsSourceSell {
    /**批次号*/
    private String batchCode;
    /**客户姓名*/
    private String customerName;
    /**承运公司*/
    private String shipCompanyName;
    /**销售数量*/
    private BigDecimal totalQuantity;
    /**销售金额*/
    private BigDecimal totalAmount;
    /**已付金额*/
    private BigDecimal paidAmount;
    /**销售员*/
    private String realName;

    public String getBatchCode() { return batchCode; }

    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public String getCustomerName() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getShipCompanyName() { return shipCompanyName; }

    public void setShipCompanyName(String shipCompanyName) { this.shipCompanyName = shipCompanyName; }

    public BigDecimal getTotalQuantity() { return totalQuantity; }

    public void setTotalQuantity(BigDecimal totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }

    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }

    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public String getRealName() { return realName; }

    public void setRealName(String realName) { this.realName = realName; }
}
