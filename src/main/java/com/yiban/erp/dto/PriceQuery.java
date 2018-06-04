package com.yiban.erp.dto;

import java.util.List;

public class PriceQuery {

    private List<Long> goodsDetailIds;
    private Long customerId;

    public List<Long> getGoodsDetailIds() {
        return goodsDetailIds;
    }

    public void setGoodsDetailIds(List<Long> goodsDetailIds) {
        this.goodsDetailIds = goodsDetailIds;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
