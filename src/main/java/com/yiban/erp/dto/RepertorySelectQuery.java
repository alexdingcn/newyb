package com.yiban.erp.dto;

import com.yiban.erp.entities.GoodsAttributeRef;
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


    private BigDecimal minQuantity; //最低库存

    private boolean byPage; // 是否分页，只有当true的时候才考虑分页，否则不考虑
    private int offset;
    private int limit;

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


}
