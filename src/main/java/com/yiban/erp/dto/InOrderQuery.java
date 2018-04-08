package com.yiban.erp.dto;

import java.util.Date;

public class InOrderQuery {
    private Integer companyId;
    private Integer warehouseId;
    private Long in_user_id;
    private Integer in_type;
    private Date in_date_min;
    private Date in_date_max;
    private Integer state;

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

    public Long getIn_user_id() {
        return in_user_id;
    }

    public void setIn_user_id(Long in_user_id) {
        this.in_user_id = in_user_id;
    }

    public Integer getIn_type() {
        return in_type;
    }

    public void setIn_type(Integer in_type) {
        this.in_type = in_type;
    }

    public Date getIn_date_min() {
        return in_date_min;
    }

    public void setIn_date_min(Date in_date_min) {
        this.in_date_min = in_date_min;
    }

    public Date getIn_date_max() {
        return in_date_max;
    }

    public void setIn_date_max(Date in_date_max) {
        this.in_date_max = in_date_max;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}