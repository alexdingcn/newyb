package com.yiban.erp.dto;

import com.yiban.erp.entities.RepertoryInBackDetail;

import java.util.Date;
import java.util.List;

public class BuyBackAddRequest {

    private Integer warehouseId;
    private Long supplierId;
    private Long supplierContactId;
    private Long buyerId;
    private Date backTime;
    private String keyWord;

    private List<RepertoryInBackDetail> details;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getSupplierContactId() {
        return supplierContactId;
    }

    public void setSupplierContactId(Long supplierContactId) {
        this.supplierContactId = supplierContactId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Date getBackTime() {
        return backTime;
    }

    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public List<RepertoryInBackDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RepertoryInBackDetail> details) {
        this.details = details;
    }
}
