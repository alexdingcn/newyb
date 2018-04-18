package com.yiban.erp.dto;

import java.util.Date;

public class RepertoryQuery {
    private Integer companyId;
    private Integer warehouseId;
    private Long in_user_id;
    private String storeState;
    private String saleState;
    private Integer isZeroStore;
    private Long supplierId;
    private Long factoryId;
    private Long goodsId;
    private Long buyerId;
    private Integer keedays;
    private Date maxInDate;
    private Date minExpDate;

    private String  goodSearch;

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

    public String getStoreState() {
        return storeState;
    }

    public void setStoreState(String storeState) {
        this.storeState = storeState;
    }

    public String getSaleState() {
        return saleState;
    }

    public void setSaleState(String saleState) {
        this.saleState = saleState;
    }

    public Integer getIsZeroStore() {
        return isZeroStore;
    }

    public void setIsZeroStore(Integer isZeroStore) {
        this.isZeroStore = isZeroStore;
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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getKeedays() {
        return keedays;
    }

    public void setKeedays(Integer keedays) {
        this.keedays = keedays;
    }

    public Date getMaxInDate() {
        return maxInDate;
    }

    public void setMaxInDate(Date maxInDate) {
        this.maxInDate = maxInDate;
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

    public Date getMinExpDate() {
        return minExpDate;
    }

    public void setMinExpDate(Date minExpDate) {
        this.minExpDate = minExpDate;
    }

    public String getGoodSearch() {
        return goodSearch;
    }

    public void setGoodSearch(String goodSearch) {
        this.goodSearch = goodSearch;
    }

    private Integer page;
    private Integer size;
    private Integer offset;


}