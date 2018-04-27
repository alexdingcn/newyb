package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryOut {
    private Long id;

    private Integer companyId;

    private Integer warehouseId;

    private String refType;

    private Date outDate;

    private Long refOrderId;

    private String refOrderNumber;

    private Long customerId;

    private String customerName;

    private Integer customerRepId;

    private String customerRepName;

    private String goTo;

    private BigDecimal totalQuantity;

    private BigDecimal totalAmount;

    private String makeOrderUser;

    private String checkOrderUser;

    private Date checkDate;

    private String fileNo;

    private String comment;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

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

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType == null ? null : refType.trim();
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public Long getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }

    public String getRefOrderNumber() {
        return refOrderNumber;
    }

    public void setRefOrderNumber(String refOrderNumber) {
        this.refOrderNumber = refOrderNumber == null ? null : refOrderNumber.trim();
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
        this.customerName = customerName == null ? null : customerName.trim();
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
        this.customerRepName = customerRepName == null ? null : customerRepName.trim();
    }

    public String getGoTo() {
        return goTo;
    }

    public void setGoTo(String goTo) {
        this.goTo = goTo == null ? null : goTo.trim();
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

    public String getMakeOrderUser() {
        return makeOrderUser;
    }

    public void setMakeOrderUser(String makeOrderUser) {
        this.makeOrderUser = makeOrderUser;
    }

    public String getCheckOrderUser() {
        return checkOrderUser;
    }

    public void setCheckOrderUser(String checkOrderUser) {
        this.checkOrderUser = checkOrderUser;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo == null ? null : fileNo.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
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
}