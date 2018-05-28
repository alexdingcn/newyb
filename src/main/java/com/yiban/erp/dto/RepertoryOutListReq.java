package com.yiban.erp.dto;

import java.util.Date;
import java.util.List;

public class RepertoryOutListReq {

    private Integer companyId;
    private List<String> outTypeList;
    private Date startOutDate;
    private Date endOutDate;
    private Integer warehouseId;
    private Long supplierId;
    private String  googsName;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public List<String> getOutTypeList() {
        return outTypeList;
    }

    public void setOutTypeList(List<String> outTypeList) {
        this.outTypeList = outTypeList;
    }

    public Date getStartOutDate() {
        return startOutDate;
    }

    public void setStartOutDate(Date startOutDate) {
        this.startOutDate = startOutDate;
    }

    public Date getEndOutDate() {
        return endOutDate;
    }

    public void setEndOutDate(Date endOutDate) {
        this.endOutDate = endOutDate;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getGoogsName() {
        return googsName;
    }

    public void setGoogsName(String googsName) {
        this.googsName = googsName;
    }
}
