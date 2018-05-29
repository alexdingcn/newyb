package com.yiban.erp.entities;

import com.yiban.erp.constant.BuyOrderStatus;

import java.util.Date;
import java.util.List;

public class BuyOrderQuery {

    private Long orderId;

    private BuyOrderStatus orderStatus;

    private Long supplierId;

    private Date startDate;

    private Date endDate;

    private BuyOrderStatus status;

    private List<String> statusNames; //查询多种状态的时候

    private String checkResult;

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BuyOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(BuyOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public List<String> getStatusNames() {
        return statusNames;
    }

    public void setStatusNames(List<String> statusNames) {
        this.statusNames = (statusNames == null || statusNames.isEmpty()) ? null : statusNames;
    }
}