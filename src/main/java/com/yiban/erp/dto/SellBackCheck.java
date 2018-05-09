package com.yiban.erp.dto;

import java.math.BigDecimal;
import java.util.Date;

public class SellBackCheck {

    private Long backOrderId;
    private String status;
    private String checkResult;

    //用于质量复核时
    private Long detailId;
    private BigDecimal rightQuantity;
    private BigDecimal badQuantity;
    private String badPlan;
    private String badReason;
    private Long checkTempMethod;
    private Date checkTime;
    private boolean checkStatus;
    private String checkUser;
    private String updatedBy;
    private Date updatedTime;

    public Long getBackOrderId() {
        return backOrderId;
    }

    public void setBackOrderId(Long backOrderId) {
        this.backOrderId = backOrderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public BigDecimal getRightQuantity() {
        return rightQuantity;
    }

    public void setRightQuantity(BigDecimal rightQuantity) {
        this.rightQuantity = rightQuantity;
    }

    public BigDecimal getBadQuantity() {
        return badQuantity;
    }

    public void setBadQuantity(BigDecimal badQuantity) {
        this.badQuantity = badQuantity;
    }

    public String getBadPlan() {
        return badPlan;
    }

    public void setBadPlan(String badPlan) {
        this.badPlan = badPlan;
    }

    public String getBadReason() {
        return badReason;
    }

    public void setBadReason(String badReason) {
        this.badReason = badReason;
    }

    public Long getCheckTempMethod() {
        return checkTempMethod;
    }

    public void setCheckTempMethod(Long checkTempMethod) {
        this.checkTempMethod = checkTempMethod;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }
}
