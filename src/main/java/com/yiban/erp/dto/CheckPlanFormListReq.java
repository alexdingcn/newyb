package com.yiban.erp.dto;

import java.util.Date;

public class CheckPlanFormListReq {
    private Integer companyId;
    private Integer warehouseId;
    private Integer checkPlanId;
    private Integer checkType;
    private Integer formState;
    private Date startDate;
    private Date endDate;

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

    public Integer getCheckPlanId() {
        return checkPlanId;
    }

    public void setCheckPlanId(Integer checkPlanId) {
        this.checkPlanId = checkPlanId;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }


    public Integer getFormState() {
        return formState;
    }

    public void setFormState(Integer formState) {
        this.formState = formState;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
