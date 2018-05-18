package com.yiban.erp.dto;

import com.yiban.erp.entities.PriceRule;

import java.util.List;

public class SavePriceReq {

    private List<Long> goodsDetailIds;

    private List<CustomerCategoryPrice> customerCategoryPrices;
    private List<PriceRule> customerPrices;

    public List<Long> getGoodsDetailIds() {
        return goodsDetailIds;
    }

    public void setGoodsDetailIds(List<Long> goodsDetailIds) {
        this.goodsDetailIds = goodsDetailIds;
    }

    public List<CustomerCategoryPrice> getCustomerCategoryPrices() {
        return customerCategoryPrices;
    }

    public void setCustomerCategoryPrices(List<CustomerCategoryPrice> customerCategoryPrices) {
        this.customerCategoryPrices = customerCategoryPrices;
    }

    public List<PriceRule> getCustomerPrices() {
        return customerPrices;
    }

    public void setCustomerPrices(List<PriceRule> customerPrices) {
        this.customerPrices = customerPrices;
    }
}
