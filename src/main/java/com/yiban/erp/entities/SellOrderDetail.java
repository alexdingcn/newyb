package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SellOrderDetail {
    private Long id;

    private Long sellOrderId;

    private Long repertoryId;

    private Integer quantity;

    private BigDecimal fixPrice;

    private BigDecimal disPrice;

    private BigDecimal free;

    private BigDecimal realPrice;

    private BigDecimal singlePrice;

    private BigDecimal amount;

    private BigDecimal taxRate;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String checkStatus;
    private String checkResult;
    private String checkUser;
    private Date checkDate;

    private RepertoryInfo repertoryInfo;
    private Long goodId;  //在设置repertoryInfo时设置, 辅助前端展示
    private String goodName; //在设置repertoryInfo时设置, 辅助前端展示
    private BigDecimal repertoryQuantity; //在设置repertoryInfo时设置, 辅助前端展示
    private String factoryName; //在设置repertoryInfo时设置, 辅助前端展示

    private Date createOrderDate;
    private String salerNickName;
    private String salerRealName;
    private String customerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(Long sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public Long getRepertoryId() {
        return repertoryId;
    }

    public void setRepertoryId(Long repertoryId) {
        this.repertoryId = repertoryId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFixPrice() {
        return fixPrice;
    }

    public void setFixPrice(BigDecimal fixPrice) {
        this.fixPrice = fixPrice;
    }

    public BigDecimal getDisPrice() {
        return disPrice;
    }

    public void setDisPrice(BigDecimal disPrice) {
        this.disPrice = disPrice;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public BigDecimal getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(BigDecimal realPrice) {
        this.realPrice = realPrice;
    }

    public BigDecimal getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(BigDecimal singlePrice) {
        this.singlePrice = singlePrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Date getCreateOrderDate() {
        return createOrderDate;
    }

    public void setCreateOrderDate(Date createOrderDate) {
        this.createOrderDate = createOrderDate;
    }

    public String getSalerNickName() {
        return salerNickName;
    }

    public void setSalerNickName(String salerNickName) {
        this.salerNickName = salerNickName;
    }

    public String getSalerRealName() {
        return salerRealName;
    }

    public void setSalerRealName(String salerRealName) {
        this.salerRealName = salerRealName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public RepertoryInfo getRepertoryInfo() {
        return repertoryInfo;
    }

    public void setRepertoryInfo(RepertoryInfo repertoryInfo) {
        this.repertoryInfo = repertoryInfo;
        if (repertoryInfo != null && repertoryInfo.getGoods() != null) {
            Goods goods = repertoryInfo.getGoods();
            this.goodId = goods.getId();
            this.goodName = goods.getName();
            this.repertoryQuantity = repertoryInfo.getQuantity();
            this.factoryName = goods.getFactory();
        }
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public BigDecimal getRepertoryQuantity() {
        return repertoryQuantity;
    }

    public void setRepertoryQuantity(BigDecimal repertoryQuantity) {
        this.repertoryQuantity = repertoryQuantity;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

}