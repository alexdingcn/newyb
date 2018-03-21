package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class BuyOrderDetail {
    private Long id;

    private Long buyOrderId;

    private Long goodsId;

    private String goodsName;

    private String origin;              // 来源
    private String jx;                  // 剂型
    private String spec;                // 规格
    private Long factoryId;             // 生产商ID
    private String factory;             // 生产商
    private String storageCondition;    // 存储条件
    private Integer goodsUnit;          // 商品单位ID
    private String unitName;            // 商品单位名称
    private Integer mediumPack;         // 中件装量
    private Integer bigPack;            // 大件装量

    private BigDecimal quantity;

    private BigDecimal buyPrice;

    private BigDecimal amount;

    private Integer unitId;

    private BigDecimal retailPrice;

    private BigDecimal shippedQuantity;

    private Boolean alreadyFapiao;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(Long buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public BigDecimal getShippedQuantity() {
        return shippedQuantity;
    }

    public void setShippedQuantity(BigDecimal shippedQuantity) {
        this.shippedQuantity = shippedQuantity;
    }

    public Boolean getAlreadyFapiao() {
        return alreadyFapiao;
    }

    public void setAlreadyFapiao(Boolean alreadyFapiao) {
        this.alreadyFapiao = alreadyFapiao;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy == null ? null : updatedBy.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getJx() {
        return jx;
    }

    public void setJx(String jx) {
        this.jx = jx;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public Integer getGoodsUnit() {
        return goodsUnit;
    }

    public void setGoodsUnit(Integer goodsUnit) {
        this.goodsUnit = goodsUnit;
    }

    public Integer getMediumPack() {
        return mediumPack;
    }

    public void setMediumPack(Integer mediumPack) {
        this.mediumPack = mediumPack;
    }

    public Integer getBigPack() {
        return bigPack;
    }

    public void setBigPack(Integer bigPack) {
        this.bigPack = bigPack;
    }
}