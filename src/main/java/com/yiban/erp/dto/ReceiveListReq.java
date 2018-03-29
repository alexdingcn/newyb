package com.yiban.erp.dto;

import java.util.Date;
import java.util.List;

public class ReceiveListReq {

    private Integer companyId;
    private List<String> statusList;
    private Date startReceiveDate;
    private Date endReceiveDate;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public Date getStartReceiveDate() {
        return startReceiveDate;
    }

    public void setStartReceiveDate(Date startReceiveDate) {
        this.startReceiveDate = startReceiveDate;
    }

    public Date getEndReceiveDate() {
        return endReceiveDate;
    }

    public void setEndReceiveDate(Date endReceiveDate) {
        this.endReceiveDate = endReceiveDate;
    }
}
