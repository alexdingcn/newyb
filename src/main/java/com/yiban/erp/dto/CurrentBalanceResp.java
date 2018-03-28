package com.yiban.erp.dto;

import java.math.BigDecimal;

public class CurrentBalanceResp {

    private BigDecimal lastPrice; //最后一次的采购价
    private Integer buyOrderCount; //当前正在下单的数量
    private BigDecimal ongoing; //在推申购订单申购数量
    private Integer ongoingCount;
    private Integer balance; //当前库存量

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

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public BigDecimal getOngoing() {
        return ongoing;
    }

    public void setOngoing(BigDecimal ongoing) {
        this.ongoing = ongoing;
    }

    public Integer getOngoingCount() {
        if (this.ongoing != null) {
            return this.ongoing.intValue();
        }
        return 0;
    }


}
