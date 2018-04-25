package com.yiban.erp.dto;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SellReviewOrderQuery {

    private Long customerId;
    private Long saleId;
    private List<String> statusList;
    private Date startDate;
    private Date endDate;

    private Integer companyId;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = DateUtils.truncate(startDate, Calendar.DATE);
        }
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = DateUtils.truncate(endDate, Calendar.DATE);
        }
        this.endDate = endDate;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
