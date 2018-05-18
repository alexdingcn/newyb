package com.yiban.erp.dto;

import java.util.List;

public class PriceQuery {

    private List<Long> goodsDetailIds;


    public List<Long> getGoodsDetailIds() {
        return goodsDetailIds;
    }

    public void setGoodsDetailIds(List<Long> goodsDetailIds) {
        this.goodsDetailIds = goodsDetailIds;
    }
}
