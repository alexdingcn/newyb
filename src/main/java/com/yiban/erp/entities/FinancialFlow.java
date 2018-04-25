package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialFlow {
    private Long id;

    private String bizNo;

    private Date logDate;

    private String logAccount;

    private String docNo;

    private String fileNo;

    private String bizType;

    private Long bizRefId;

    private String bizRefNo;

    private String keyWord;

    private BigDecimal logAmount;

    private BigDecimal outAmount;

    private BigDecimal inAmount;

    private BigDecimal companyAmount;

    private BigDecimal custAmount;

    private Long payMethod;

    private String payAccount;

    private String receiveAccount;

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

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo == null ? null : bizNo.trim();
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getLogAccount() {
        return logAccount;
    }

    public void setLogAccount(String logAccount) {
        this.logAccount = logAccount == null ? null : logAccount.trim();
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo == null ? null : docNo.trim();
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo == null ? null : fileNo.trim();
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType == null ? null : bizType.trim();
    }

    public Long getBizRefId() {
        return bizRefId;
    }

    public void setBizRefId(Long bizRefId) {
        this.bizRefId = bizRefId;
    }

    public String getBizRefNo() {
        return bizRefNo;
    }

    public void setBizRefNo(String bizRefNo) {
        this.bizRefNo = bizRefNo;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord == null ? null : keyWord.trim();
    }

    public BigDecimal getLogAmount() {
        return logAmount;
    }

    public void setLogAmount(BigDecimal logAmount) {
        this.logAmount = logAmount;
    }

    public BigDecimal getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(BigDecimal outAmount) {
        this.outAmount = outAmount;
    }

    public BigDecimal getInAmount() {
        return inAmount;
    }

    public void setInAmount(BigDecimal inAmount) {
        this.inAmount = inAmount;
    }

    public BigDecimal getCompanyAmount() {
        return companyAmount;
    }

    public void setCompanyAmount(BigDecimal companyAmount) {
        this.companyAmount = companyAmount;
    }

    public BigDecimal getCustAmount() {
        return custAmount;
    }

    public void setCustAmount(BigDecimal custAmount) {
        this.custAmount = custAmount;
    }

    public Long getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Long payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount == null ? null : payAccount.trim();
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount == null ? null : receiveAccount.trim();
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