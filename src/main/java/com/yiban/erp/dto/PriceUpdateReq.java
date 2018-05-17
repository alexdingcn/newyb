package com.yiban.erp.dto;

import java.math.BigDecimal;
import java.util.List;

public class PriceUpdateReq {

    private Long infoId;
    private BigDecimal batchPrice;
    private BigDecimal retailPrice;
    private BigDecimal inPrice;

    List<Long> detailIds; //如果不存在或者为空列表时，认为是全部修改详情的基础价格

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public BigDecimal getBatchPrice() {
        return batchPrice;
    }

    public void setBatchPrice(BigDecimal batchPrice) {
        this.batchPrice = batchPrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public BigDecimal getInPrice() {
        return inPrice;
    }

    public void setInPrice(BigDecimal inPrice) {
        this.inPrice = inPrice;
    }

    public List<Long> getDetailIds() {
        return detailIds;
    }

    public void setDetailIds(List<Long> detailIds) {
        this.detailIds = detailIds;
    }
}
