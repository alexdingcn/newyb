package com.yiban.erp.dto;

import java.util.Date;

public class SellBackCheck {

    private Long backOrderId;
    private String status;
    private String checkResult;

    //用于质量复核时
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
}
