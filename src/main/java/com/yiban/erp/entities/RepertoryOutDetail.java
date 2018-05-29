package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RepertoryOutDetail {
    private Long id;

    private Long repertoryOutId;

    private Long repertoryInfoId;

    private Long goodsId;

    private String batchCode; //批次号

    private String location; //库位

    private Date productDate; //生产日期

    private Date expDate; //有效期至

    private BigDecimal quantity;

    private BigDecimal free;

    private BigDecimal price;

    private BigDecimal disPrice;

    private BigDecimal amount;

    private BigDecimal taxRate;

    private Boolean checkStatus;

    private Long reviewUserId;

    private Long reviewNextUserId;

    private String reviewResult;

    private String status;

    private String checkUser;

    private Date checkDate;

    private String checkResult;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private RepertoryOut repertoryOut;

    private Goods goods;
    //展示字段
    private String refTypeName;

    private String goodsName;
    private String origin;
    private String jx;
    private String spec;
    private String factory;
    private String unitName;
    private String packUnitName;
    private BigDecimal bigPack;
    //private String permit;
    private String brandName; //商标编号
    private String storageCondition;
    private String customerName;
    private Integer customerRepId;
    private String customerRepName;
    private String warehouseName;

    public Goods getGoods() {
        return goods;
    }


    public void setGoods(Goods goods) {
        this.goods = goods;
        if (goods != null) {
            this.goodsName = goods.getName();
            this.factory = goods.getFactoryName();
            this.origin = goods.getOrigin();
            this.jx = goods.getJxName();
            this.unitName = goods.getUnitName();
            this.packUnitName = goods.getPackUnitName();
            this.brandName=goods.getBrandName();
          //  this.baseMedName = goods.getBaseMedName();
            this.storageCondition = goods.getStorageConditionName();
            this.taxRate = goods.getOutTax();
        }
    }
    public void setRepertoryOut(RepertoryOut repertoryOut) {
        this.repertoryOut = repertoryOut;

        if (repertoryOut != null) {
            this.refTypeName = repertoryOut.getRefTypeName();
            this.customerName=repertoryOut.getCustomerName();
            this.customerRepName=repertoryOut.getCustomerRepName();
            this.customerRepId=repertoryOut.getCustomerRepId();
            this.warehouseName=repertoryOut.getWarehouseName();
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepertoryOutId() {
        return repertoryOutId;
    }

    public void setRepertoryOutId(Long repertoryOutId) {
        this.repertoryOutId = repertoryOutId;
    }

    public Long getRepertoryInfoId() {
        return repertoryInfoId;
    }

    public void setRepertoryInfoId(Long repertoryInfoId) {
        this.repertoryInfoId = repertoryInfoId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDisPrice() {
        return disPrice;
    }

    public void setDisPrice(BigDecimal disPrice) {
        this.disPrice = disPrice;
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

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser == null ? null : checkUser.trim();
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult == null ? null : checkResult.trim();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy == null ? null : updatedBy.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

//    public List<RepertoryOutDetail> getDetails() {
//        return details;
//    }
//
//    public void setDetails(List<RepertoryOutDetail> details) {
//        this.details = details;
//    }


    public Boolean getSpecialManage() {
        return this.goods == null ? null : this.goods.getSpecialManage();
    }

    public String getStorageCondition() {
        return this.goods == null ? null : this.goods.getStorageConditionName();
    }

    public String getGoodsName() {
        return this.goods == null ? null : this.goods.getName();
    }

    public String getOrigin() {
        return this.goods == null ? null : this.goods.getOrigin();
    }

    public String getJx() {
        return this.goods == null ? null : this.goods.getJxName();
    }

    public String getUnitName() {
        return this.goods == null ? null : this.goods.getUnitName();
    }

    public String getPackUnitName() {
        return this.goods == null ? null : this.goods.getPackUnitName();
    }

    public BigDecimal getBigPack() {
        return this.goods == null ? null : this.goods.getBigPack();
    }

    public Boolean getCheckStatus() { return checkStatus; }

    public void setCheckStatus(Boolean checkStatus) {this.checkStatus = checkStatus; }

    public Long getReviewUserId() { return reviewUserId; }

    public void setReviewUserId(Long reviewUserId) { this.reviewUserId = reviewUserId; }

    public Long getReviewNextUserId() { return reviewNextUserId; }

    public void setReviewNextUserId(Long reviewNextUserId) { this.reviewNextUserId = reviewNextUserId; }

    public String getReviewResult() { return reviewResult; }

    public void setReviewResult(String reviewResult) { this.reviewResult = reviewResult; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getRefTypeName() {
        return refTypeName;
    }

    public void setRefTypeName(String refTypeName) {
        this.refTypeName = refTypeName;
    }

    public RepertoryOut getRepertoryOut() {
        return repertoryOut;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerRepName() {
        return customerRepName;
    }

    public void setCustomerRepName(String customerRepName) {
        this.customerRepName = customerRepName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}