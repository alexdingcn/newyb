package com.yiban.erp.dto;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class RepertorySelectQuery {

    private Integer companyId;
    private Integer warehouseId;
    private Long goodsId;
    private String batchCode; //模糊匹配
    private Long supplierId;
    private Integer page;
    private Integer size;
    private boolean useBatchCode;

    private BigDecimal minQuantity; //最低库存

    private boolean byPage; // 是否分页，只有当true的时候才考虑分页，否则不考虑
    private int offset;
    private int limit;


    //根据关联类型和管理单号，获取关联单的所有存库明细
    private String refType;
    private Long refOrderId;

    public int getOffset() {
        return (this.page == null || this.page <= 0 ? 0 : this.page - 1) * this.getLimit();
    }

    public int getLimit() {
        return (this.size == null || this.size <= 0) ? 50 : this.size;
    }

    public boolean getByPage() {
        return byPage;
    }

    public void setByPage(boolean byPage) {
        this.byPage = byPage;
    }

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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getBatchCode() {
        return StringUtils.isEmpty(batchCode) ? null : batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
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

    public BigDecimal getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(BigDecimal minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public Long getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }

    public boolean isUseBatchCode() {
        return useBatchCode;
    }

    public void setUseBatchCode(boolean useBatchcode) {
        this.useBatchCode = useBatchcode;
    }
}
