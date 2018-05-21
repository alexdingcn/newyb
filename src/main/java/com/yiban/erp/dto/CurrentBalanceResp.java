package com.yiban.erp.dto;

import java.math.BigDecimal;

public class CurrentBalanceResp {

    private BigDecimal lastPrice; //最后一次的采购价
    private Integer buyOrderCount; //当前正在下单的数量
    private BigDecimal ongoingCount; //在推申购订单申购数量
    private BigDecimal balance; //当前库存量


    private BigDecimal lastSalePrice; //最后一次销售价，用户goods列表中查询映射

    private Long goodsId;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Integer getBuyOrderCount() {
        return buyOrderCount;
    }

    public void setBuyOrderCount(Integer buyOrderCount) {
        this.buyOrderCount = buyOrderCount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getOngoingCount() {
        return ongoingCount;
    }

    public void setOngoingCount(BigDecimal ongoingCount) {
        this.ongoingCount = ongoingCount;
    }

    public BigDecimal getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(BigDecimal lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }
}
