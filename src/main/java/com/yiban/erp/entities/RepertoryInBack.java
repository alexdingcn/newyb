package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RepertoryInBack {
    private Long id;

    private Integer companyId;

    private String refType;

    private String orderNumber;

    private Long inOrderId;
    private String inOrderNumber;
    private Date inReceiveTime;

    private Long supplierId;
    private String supplierName;

    private Long supplierContactId;
    private String supplierContactName;

    private Long buyerId;

    private String buyerName;

    private String status;

    private Date backTime;

    private String keyWord;

    private Integer warehouseId;
    private String warehouseName;

    private String backBuyUser;

    private Date backBuyTime;

    private String backBuyResult;

    private String backQualityUser;

    private Date backQualityTime;

    private String backQualityResult;

    private Date qualityCheckTime;

    private String qualityCheckUser;

    private Date finalCheckTime;

    private String finalCheckUser;

    private BigDecimal totalQuantity;

    private BigDecimal totalAmount;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private List<RepertoryInBackDetail> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType == null ? null : refType.trim();
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber == null ? null : orderNumber.trim();
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getSupplierContactId() {
        return supplierContactId;
    }

    public void setSupplierContactId(Long supplierContactId) {
        this.supplierContactId = supplierContactId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getBackTime() {
        return backTime;
    }

    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getBackBuyUser() {
        return backBuyUser;
    }

    public void setBackBuyUser(String backBuyUser) {
        this.backBuyUser = backBuyUser == null ? null : backBuyUser.trim();
    }

    public Date getBackBuyTime() {
        return backBuyTime;
    }

    public void setBackBuyTime(Date backBuyTime) {
        this.backBuyTime = backBuyTime;
    }

    public String getBackBuyResult() {
        return backBuyResult;
    }

    public void setBackBuyResult(String backBuyResult) {
        this.backBuyResult = backBuyResult == null ? null : backBuyResult.trim();
    }

    public String getBackQualityUser() {
        return backQualityUser;
    }

    public void setBackQualityUser(String backQualityUser) {
        this.backQualityUser = backQualityUser == null ? null : backQualityUser.trim();
    }

    public Date getBackQualityTime() {
        return backQualityTime;
    }

    public void setBackQualityTime(Date backQualityTime) {
        this.backQualityTime = backQualityTime;
    }

    public String getBackQualityResult() {
        return backQualityResult;
    }

    public void setBackQualityResult(String backQualityResult) {
        this.backQualityResult = backQualityResult == null ? null : backQualityResult.trim();
    }

    public Date getQualityCheckTime() {
        return qualityCheckTime;
    }

    public void setQualityCheckTime(Date qualityCheckTime) {
        this.qualityCheckTime = qualityCheckTime;
    }

    public String getQualityCheckUser() {
        return qualityCheckUser;
    }

    public void setQualityCheckUser(String qualityCheckUser) {
        this.qualityCheckUser = qualityCheckUser == null ? null : qualityCheckUser.trim();
    }

    public Date getFinalCheckTime() {
        return finalCheckTime;
    }

    public void setFinalCheckTime(Date finalCheckTime) {
        this.finalCheckTime = finalCheckTime;
    }

    public String getFinalCheckUser() {
        return finalCheckUser;
    }

    public void setFinalCheckUser(String finalCheckUser) {
        this.finalCheckUser = finalCheckUser == null ? null : finalCheckUser.trim();
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Long getInOrderId() {
        return inOrderId;
    }

    public void setInOrderId(Long inOrderId) {
        this.inOrderId = inOrderId;
    }

    public String getInOrderNumber() {
        return inOrderNumber;
    }

    public void setInOrderNumber(String inOrderNumber) {
        this.inOrderNumber = inOrderNumber;
    }

    public Date getInReceiveTime() {
        return inReceiveTime;
    }

    public void setInReceiveTime(Date inReceiveTime) {
        this.inReceiveTime = inReceiveTime;
    }

    public List<RepertoryInBackDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RepertoryInBackDetail> details) {
        this.details = details;
    }
}