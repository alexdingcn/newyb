package com.yiban.erp.dto;


import com.yiban.erp.constant.InventoryStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InventorySearch {

    private Integer companyId;

    private String searchValue;

    private Integer warehouseId;

    private List<InventoryStatus> statusList;

    private Date startDate;

    private Date endDate;

    private Integer page;

    private Integer size;

    private Integer limit;
    private Integer offset;

    public Integer getLimit() {
        if (this.size == null || this.size <=0 ) {
            return null;
        }else {
            return this.size;
        }
    }

    public Integer getOffset() {
        if (this.getLimit() == null) {
            return null;
        }else {
            return (this.page == null || this.page <= 0 ? 0 : this.page - 1) * this.getLimit();
        }
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getSearchValue() {
        if (StringUtils.isEmpty(searchValue)) {
            return null;
        }
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<InventoryStatus> getStatusList() {
        if (statusList == null || statusList.isEmpty()) {
            return null;
        }
        return statusList;
    }

    public void setStatusList(List<InventoryStatus> statusList) {
        this.statusList = statusList;
    }

    public Date getStartDate() {
        if (startDate != null) {
            return DateUtils.truncate(startDate, Calendar.DATE);
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        if (endDate != null) {
            return DateUtils.truncate(DateUtils.addDays(endDate, 1), Calendar.DATE);
        }
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
