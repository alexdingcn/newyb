package com.yiban.erp.querybean;

import java.util.Date;

public class RepertoryQuery {
    private Integer companyId;
    private Long warehouseId;
    private Long in_user_id;
    private String store_state;
    private String counter_state;
    private String sale_state;
    private Integer is_zero_store;
    private Long supplierId;
    private Long factoryId;
    private Long good_id;
    private Long buyerId;
    private Integer keedays;
    private Date max_in_date;
    private Date min_exp_date;

    private Integer page;
    private Integer size;
    private Integer offset;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getStore_state() {
        return store_state;
    }

    public void setStore_state(String store_state) {
        this.store_state = store_state;
    }

    public String getCounter_state() {
        return counter_state;
    }

    public void setCounter_state(String counter_state) {
        this.counter_state = counter_state;
    }

    public String getSale_state() {
        return sale_state;
    }

    public void setSale_state(String sale_state) {
        this.sale_state = sale_state;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getGood_id() {
        return good_id;
    }

    public void setGood_id(Long good_id) {
        this.good_id = good_id;
    }

    public Integer getKeedays() {
        return keedays;
    }

    public void setKeedays(Integer keedays) {
        this.keedays = keedays;
    }

    public Long getIn_user_id() {
        return in_user_id;
    }

    public void setIn_user_id(Long in_user_id) {
        this.in_user_id = in_user_id;
    }

    public Integer getIs_zero_store() {
        return is_zero_store;
    }

    public void setIs_zero_store(Integer is_zero_store) {
        this.is_zero_store = is_zero_store;
    }

    public Date getMax_in_date() {
        return max_in_date;
    }

    public void setMax_in_date(Date max_in_date) {
        this.max_in_date = max_in_date;
    }

    public Date getMin_exp_date() {
        return min_exp_date;
    }

    public void setMin_exp_date(Date min_exp_date) {
        this.min_exp_date = min_exp_date;
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

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}