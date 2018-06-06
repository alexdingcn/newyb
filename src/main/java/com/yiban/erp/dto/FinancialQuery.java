package com.yiban.erp.dto;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class FinancialQuery {

    private Integer companyId;


    private Date logStartDate;
    private Date logEndDate;
    private Date createdStartTime;
    private Date createdEndTime;
    private String custType;
    private Long custId;
    private Integer page;
    private Integer pageSize;

    private String status; // 预付款/预收款时查询使用
    private String preBizType; //标识预收款还是预付款, 根据该值进行区分不同的操作

    private int offset;
    private int limit;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Date getLogStartDate() {
        if (this.logStartDate != null) {
            return DateUtils.truncate(this.logStartDate, Calendar.DATE);
        }
        return logStartDate;
    }

    public void setLogStartDate(Date logStartDate) {
        this.logStartDate = logStartDate;
    }

    public Date getLogEndDate() {
        if (this.logEndDate != null) {
            return DateUtils.truncate(DateUtils.addDays(this.logEndDate, 1), Calendar.DATE);
        }
        return logEndDate;
    }

    public void setLogEndDate(Date logEndDate) {
        this.logEndDate = logEndDate;
    }

    public Date getCreatedStartTime() {
        if (this.createdStartTime != null) {
            return DateUtils.truncate(this.createdStartTime, Calendar.DATE);
        }
        return createdStartTime;
    }

    public void setCreatedStartTime(Date createdStartTime) {
        this.createdStartTime = createdStartTime;
    }

    public Date getCreatedEndTime() {
        if (this.createdEndTime != null) {
            return DateUtils.truncate(DateUtils.addDays(this.createdEndTime, 1), Calendar.DATE);
        }
        return createdEndTime;
    }

    public void setCreatedEndTime(Date createdEndTime) {
        this.createdEndTime = createdEndTime;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public int getOffset() {
        return (this.page == null || this.page <= 0 ? 0 : this.page - 1) * this.getLimit();
    }

    public int getLimit() {
        return (this.pageSize == null || this.pageSize <= 0) ? 50 : this.pageSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPreBizType() {
        return preBizType;
    }

    public void setPreBizType(String preBizType) {
        this.preBizType = preBizType;
    }
}
