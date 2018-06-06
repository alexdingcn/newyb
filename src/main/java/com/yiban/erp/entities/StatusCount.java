package com.yiban.erp.entities;

import java.text.DecimalFormat;
import java.util.Date;

public class StatusCount {
    private String status;
    private Integer count;
    private Double amount;
    private Date tradeDate;
    private String goodsName;
    private Long goodsId;
    private Integer goodsCount;
    private Integer orderCount;
    private Long customerId;
    private String customerName;
    private Double customerReceivable;

    private Double avgOrderPrice;

    private Date earliestOrderDate;
    private Date latestOrderDate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Double getCustomerReceivable() {
        return customerReceivable;
    }

    public void setCustomerReceivable(Double customerReceivable) {
        this.customerReceivable = customerReceivable;
    }

    public Date getEarliestOrderDate() {
        return earliestOrderDate;
    }

    public void setEarliestOrderDate(Date earliestOrderDate) {
        this.earliestOrderDate = earliestOrderDate;
    }

    public Date getLatestOrderDate() {
        return latestOrderDate;
    }

    public void setLatestOrderDate(Date latestOrderDate) {
        this.latestOrderDate = latestOrderDate;
    }

    public String getAvgOrderAmount() {
        if (orderCount != null && orderCount > 0 && amount != null) {
            DecimalFormat formatter = new DecimalFormat("#0.00");
            return formatter.format(amount / orderCount);
        }
        return "0.00";
    }

    public int getAvgOrderGap() {
        if (earliestOrderDate != null) {
            double diffInDays = (new Date().getTime() - earliestOrderDate.getTime()) / (1000 * 60 * 60 * 24);
            if (diffInDays > 0 && orderCount > 0) {
                return (int)(diffInDays / orderCount);
            }
        }

        return 0;
    }
}
