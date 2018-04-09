package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryCheckPlanDetail {
    private Long id;

    private Long checkPlanId;

    private String formNo;

    private String formStatus;

    private Long repertoryInfoId;

    private Long goodsId;

    private String batchCode;

    private BigDecimal accLimit;

    private BigDecimal checkLimit;

    private String checkNote;

    private Integer checkStatus;

    private BigDecimal price;

    private BigDecimal amount;

    private Date productDate;

    private Date expDate;

    private String createdBy;

    private Date createdTime;

    private String updatedBy;

    private Date updatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCheckPlanId() {
        return checkPlanId;
    }

    public void setCheckPlanId(Long checkPlanId) {
        this.checkPlanId = checkPlanId;
    }

    public String getFormNo() {
        return formNo;
    }

    public void setFormNo(String formNo) {
        this.formNo = formNo == null ? null : formNo.trim();
    }

    public String getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(String formStatus) {
        this.formStatus = formStatus == null ? null : formStatus.trim();
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
        this.batchCode = batchCode == null ? null : batchCode.trim();
    }

    public BigDecimal getAccLimit() {
        return accLimit;
    }

    public void setAccLimit(BigDecimal accLimit) {
        this.accLimit = accLimit;
    }

    public BigDecimal getCheckLimit() {
        return checkLimit;
    }

    public void setCheckLimit(BigDecimal checkLimit) {
        this.checkLimit = checkLimit;
    }

    public String getCheckNote() {
        return checkNote;
    }

    public void setCheckNote(String checkNote) {
        this.checkNote = checkNote == null ? null : checkNote.trim();
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy == null ? null : updatedBy.trim();
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
}