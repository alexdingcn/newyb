package com.yiban.erp.querybean;

public class RepertoryQuery {
    private Integer companyId;
    private Long orderId;
    private Long warehouseId;
    private Integer store_state;
    private Integer counter_state;
    private Integer sale_state;
    private Long supplierId;
    private Long factoryId;
    private Long buyerId;

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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getStore_state() {
        return store_state;
    }

    public void setStore_state(Integer store_state) {
        this.store_state = store_state;
    }

    public Integer getCounter_state() {
        return counter_state;
    }

    public void setCounter_state(Integer counter_state) {
        this.counter_state = counter_state;
    }

    public Integer getSale_state() {
        return sale_state;
    }

    public void setSale_state(Integer sale_state) {
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
}