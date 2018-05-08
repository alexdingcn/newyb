package com.yiban.erp.dto;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SellBackQuery {

    private Integer companyId;
    private Integer warehouseId;
    private Long customerId;
    private Long saleId;
    private List<String> statusList;
    private Date createdStartTime;
    private Date createdEndTime;

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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public Date getCreatedStartTime() {
        if (createdStartTime != null) {
            return DateUtils.truncate(createdStartTime, Calendar.DATE);
        }
        return createdStartTime;
    }

    public void setCreatedStartTime(Date createdStartTime) {
        this.createdStartTime = createdStartTime;
    }

    public Date getCreatedEndTime() {
        if (createdEndTime != null) {
            return DateUtils.truncate(DateUtils.addDays(createdEndTime, 1), Calendar.DATE);
        }
        return createdEndTime;
    }

    public void setCreatedEndTime(Date createdEndTime) {
        this.createdEndTime = createdEndTime;
    }
}
