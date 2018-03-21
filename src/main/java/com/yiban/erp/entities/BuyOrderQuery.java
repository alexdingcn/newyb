package com.yiban.erp.entities;

import com.yiban.erp.constant.BuyOrderStatus;

import java.util.Date;

public class BuyOrderQuery {

    private Long supplierId;

    private Date startDate;

    private Date endDate;

    private BuyOrderStatus status;

    private Integer companyId;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
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

    public BuyOrderStatus getStatus() {
        return status;
    }

    public void setStatus(BuyOrderStatus status) {
        this.status = status;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}