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

    private String reviewUser;

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
    private RepertoryInfo repertoryInfo;

    //展示字段
    private String refTypeName;

    private String goodsName;
    private String origin;
    private String factoryName;
    private String unitName;
    private String packUnitName;
    private BigDecimal bigPack;
    private String brandName; //商标编号
    private String storageCondition;
    private String customerName;
    private Integer customerRepId;
    private String customerRepName;
    private String customerTel;
    private String warehouseName;




    public void setGoods(Goods goods) {
        this.goods = goods;
        if (goods != null) {
            this.goodsName = goods.getName();
            this.factoryName = goods.getFactoryName();
            this.origin = goods.getOrigin();
            this.unitName = goods.getUnitName();
            this.packUnitName = goods.getPackUnitName();
            this.brandName=goods.getBrandName();
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
            this.customerTel=repertoryOut.getCustomerTel();
        }
    }
    public void setRepertoryInfo(RepertoryInfo repertoryInfo) {
        this.repertoryInfo = repertoryInfo;
        if (repertoryInfo != null) {
            this.goodsName = repertoryInfo.getGoodsName();
            this.factoryName = repertoryInfo.getFactoryName();
            this.origin = repertoryInfo.getOrigin();
            this.unitName = repertoryInfo.getUnitName();
            this.storageCondition = repertoryInfo.getStorageConditionName();
            this.taxRate = repertoryInfo.getTaxRate();
        }
    }

    public RepertoryOut getRepertoryOut() {
        return repertoryOut;
    }

    public Goods getGoods() {
        return goods;
    }

    public RepertoryInfo getRepertoryInfo() {
        return repertoryInfo;
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

    public Boolean getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getReviewUser() { return reviewUser; }

    public void setReviewUser(String reviewUser) { this.reviewUser = reviewUser; }

    public Long getReviewNextUserId() {
        return reviewNextUserId;
    }

    public void setReviewNextUserId(Long reviewNextUserId) {
        this.reviewNextUserId = reviewNextUserId;
    }

    public String getReviewResult() {
        return reviewResult;
    }

    public void setReviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

    public String getRefTypeName() {
        return refTypeName;
    }

    public void setRefTypeName(String refTypeName) {
        this.refTypeName = refTypeName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getPackUnitName() {
        return packUnitName;
    }

    public void setPackUnitName(String packUnitName) {
        this.packUnitName = packUnitName;
    }

    public BigDecimal getBigPack() {
        return bigPack;
    }

    public void setBigPack(BigDecimal bigPack) {
        this.bigPack = bigPack;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustomerRepId() {
        return customerRepId;
    }

    public void setCustomerRepId(Integer customerRepId) {
        this.customerRepId = customerRepId;
    }

    public String getCustomerRepName() {
        return customerRepName;
    }

    public void setCustomerRepName(String customerRepName) {
        this.customerRepName = customerRepName;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    @Override
    public String toString() {
        return "RepertoryOutDetail{" +
                "id=" + id +
                ", repertoryOutId=" + repertoryOutId +
                ", repertoryInfoId=" + repertoryInfoId +
                ", goodsId=" + goodsId +
                ", batchCode='" + batchCode + '\'' +
                ", location='" + location + '\'' +
                ", productDate=" + productDate +
                ", expDate=" + expDate +
                ", quantity=" + quantity +
                ", free=" + free +
                ", price=" + price +
                ", disPrice=" + disPrice +
                ", amount=" + amount +
                ", taxRate=" + taxRate +
                ", checkStatus=" + checkStatus +
                ", reviewUser='" + reviewUser + '\'' +
                ", reviewNextUserId=" + reviewNextUserId +
                ", reviewResult='" + reviewResult + '\'' +
                ", status='" + status + '\'' +
                ", checkUser='" + checkUser + '\'' +
                ", checkDate=" + checkDate +
                ", checkResult='" + checkResult + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", repertoryOut=" + repertoryOut +
                ", goods=" + goods +
                ", repertoryInfo=" + repertoryInfo +
                ", refTypeName='" + refTypeName + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", origin='" + origin + '\'' +
                ", factoryName='" + factoryName + '\'' +
                ", unitName='" + unitName + '\'' +
                ", packUnitName='" + packUnitName + '\'' +
                ", bigPack=" + bigPack +
                ", brandName='" + brandName + '\'' +
                ", storageCondition='" + storageCondition + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerRepId=" + customerRepId +
                ", customerRepName='" + customerRepName + '\'' +
                ", customerTel='" + customerTel + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                '}';
    }
}