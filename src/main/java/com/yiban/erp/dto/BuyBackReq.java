package com.yiban.erp.dto;

import java.util.Date;
import java.util.List;

public class BuyBackReq {

    private Integer companyId;
    private Long warehouseId;
    private Date createdStartTime;
    private Date createdEndTime;
    private List<String> searchStatus;

    // 检查结果提交
    private Long backId;
    private String type;
    private String checkResult;

    private boolean detailCheckStatus;
    private String updatedBy;
    private Date updatedTime;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Date getCreatedStartTime() {
        return createdStartTime;
    }

    public void setCreatedStartTime(Date createdStartTime) {
        this.createdStartTime = createdStartTime;
    }

    public Date getCreatedEndTime() {
        return createdEndTime;
    }

    public void setCreatedEndTime(Date createdEndTime) {
        this.createdEndTime = createdEndTime;
    }

    public List<String> getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(List<String> searchStatus) {
        this.searchStatus = searchStatus;
    }

    public Long getBackId() {
        return backId;
    }

    public void setBackId(Long backId) {
        this.backId = backId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public boolean isDetailCheckStatus() {
        return detailCheckStatus;
    }

    public void setDetailCheckStatus(boolean detailCheckStatus) {
        this.detailCheckStatus = detailCheckStatus;
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
}
