package com.yiban.erp.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class SellOrderQuery {

    private Integer companyId;
    private Long sellOrderId;
    private Long customerId;
    private Long saleId;
    private String refNo;
    private Integer page;
    private Integer pageSize;
    private String status;
    private Date formCreateOrderDate;
    private Date toCreateOrderDate;

    private Integer offset;
    private Integer limit;

    public Integer getOffset() {
        return (page == null || page <= 0 ? 0 : page - 1) * getLimit();
    }

    public Integer getLimit() {
        return (pageSize == null || pageSize <= 0) ? 20 : pageSize;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
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

    public String getRefNo() {
        return StringUtils.isNotBlank(refNo.trim()) ? this.refNo.trim() : null;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFormCreateOrderDate() {
        if (formCreateOrderDate != null) {
            return DateUtils.truncate(formCreateOrderDate, Calendar.DATE);
        }
        return formCreateOrderDate;
    }

    public void setFormCreateOrderDate(Date formCreateOrderDate) {
        this.formCreateOrderDate = formCreateOrderDate;
    }

    public Date getToCreateOrderDate() {
        if (toCreateOrderDate != null) {
            return DateUtils.truncate(DateUtils.addDays(toCreateOrderDate, 1), Calendar.DATE);
        }
        return toCreateOrderDate;
    }

    public void setToCreateOrderDate(Date toCreateOrderDate) {
        this.toCreateOrderDate = toCreateOrderDate;
    }

    public Long getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(Long sellOrderId) {
        this.sellOrderId = sellOrderId;
    }
}
