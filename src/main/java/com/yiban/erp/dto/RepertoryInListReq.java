package com.yiban.erp.dto;

import java.util.Date;
import java.util.List;

public class RepertoryInListReq {

    private Integer companyId;
    private List<String> inTypeList;
    private String inStatus;
    private Date startInDate;
    private Date endInDate;
    private Integer warehouseId;

    public String getInStatus() {
        return inStatus;
    }

    public void setInStatus(String inStatus) {
        this.inStatus = inStatus;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public List<String> getInTypeList() {
        return inTypeList;
    }

    public void setInTypeList(List<String> inTypeList) {
        this.inTypeList = inTypeList;
    }

    public Date getStartInDate() {
        return startInDate;
    }

    public void setStartInDate(Date startInDate) {
        this.startInDate = startInDate;
    }

    public Date getEndInDate() {
        return endInDate;
    }

    public void setEndInDate(Date endInDate) {
        this.endInDate = endInDate;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }
}
