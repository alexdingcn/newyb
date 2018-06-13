package com.yiban.erp.entities;


import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品溯源采?记录实体类
 * @author wk
 */
public class GoodsSourceBuy {
    /**批次号*/
    private String batchCode;
    /**c采购数量*/
    private BigDecimal buyOrderQuality;
    /**入库数量*/
    private BigDecimal inCount;
    /**库存量*/
    private BigDecimal quantity;
    /**采购单价*/
    private BigDecimal buyPrice;
    /**入库时间*/
    private Date createTime;
    /**供应商*/
    private String shipName;
    /**采购员*/
    private String realName;

    public String getBatchCode() { return batchCode; }

    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public BigDecimal getBuyOrderQuality() { return buyOrderQuality; }

    public void setBuyOrderQuality(BigDecimal buyOrderQuality) { this.buyOrderQuality = buyOrderQuality; }

    public BigDecimal getInCount() { return inCount; }

    public void setInCount(BigDecimal inCount) { this.inCount = inCount; }

    public BigDecimal getQuantity() { return quantity; }

    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getBuyPrice() { return buyPrice; }

    public void setBuyPrice(BigDecimal buyPrice) { this.buyPrice = buyPrice; }

    public Date getCreateTime() { return createTime; }

    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getShipName() { return shipName; }

    public void setShipName(String shipName) { this.shipName = shipName; }

    public String getRealName() { return realName; }

    public void setRealName(String realName) { this.realName = realName; }
}
