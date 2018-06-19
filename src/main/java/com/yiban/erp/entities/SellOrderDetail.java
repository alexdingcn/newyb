package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class SellOrderDetail implements Cloneable {
    private Long id;

    private Long sellOrderId;

    private Long repertoryId;

    private BigDecimal quantity;

    private BigDecimal fixPrice;

    private BigDecimal disPrice;

    private BigDecimal free;

    private BigDecimal realPrice;

    private BigDecimal singlePrice;

    private BigDecimal amount;

    private BigDecimal taxRate;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String batchCode;
    private Long goodsId;
    private Date productDate;
    private Date expDate;
    private String location;
    private BigDecimal backQuantity; //已退货数

    private String checkStatus;
    private String checkResult;
    private String checkUser;
    private Date checkDate;

    private Date createOrderDate;
    private String saleNickName;
    private String saleRealName;
    private String customerName;

    private Goods goods;

    private BigDecimal repertoryQuantity; // 当前库存量
    private BigDecimal onWayQuantity; // 当前在单数量

    //展示字段
    private String goodsName;
    private String factoryName;
    private String origin;
    private String jx;
    private String spec;
    private String unitName;
    private String permitNo;
    private String baseMedName;
    private String storageConditionName;

    public void setGoods(Goods goods) {
        this.goods = goods;
        if (goods != null) {
            this.goodsName = goods.getName();
            this.factoryName = goods.getFactoryName();
            this.origin = goods.getOrigin();
            this.jx = goods.getJxName();
            this.unitName = goods.getUnitName();
            this.baseMedName = goods.getBaseMedName();
            this.storageConditionName = goods.getStorageConditionName();
            this.taxRate = goods.getOutTax();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(Long sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public Long getRepertoryId() {
        return repertoryId;
    }

    public void setRepertoryId(Long repertoryId) {
        this.repertoryId = repertoryId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFixPrice() {
        return fixPrice;
    }

    public void setFixPrice(BigDecimal fixPrice) {
        this.fixPrice = fixPrice;
    }

    public BigDecimal getDisPrice() {
        return disPrice;
    }

    public void setDisPrice(BigDecimal disPrice) {
        this.disPrice = disPrice;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public BigDecimal getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(BigDecimal realPrice) {
        this.realPrice = realPrice;
    }

    public BigDecimal getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(BigDecimal singlePrice) {
        this.singlePrice = singlePrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Date getCreateOrderDate() {
        return createOrderDate;
    }

    public void setCreateOrderDate(Date createOrderDate) {
        this.createOrderDate = createOrderDate;
    }

    public String getSaleNickName() {
        return saleNickName;
    }

    public void setSaleNickName(String saleNickName) {
        this.saleNickName = saleNickName;
    }

    public String getSaleRealName() {
        return saleRealName;
    }

    public void setSaleRealName(String saleRealName) {
        this.saleRealName = saleRealName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodId) {
        this.goodsId = goodId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getRepertoryQuantity() {
        return repertoryQuantity;
    }

    public void setRepertoryQuantity(BigDecimal repertoryQuantity) {
        this.repertoryQuantity = repertoryQuantity;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getBackQuantity() {
        return backQuantity;
    }

    public void setBackQuantity(BigDecimal backQuantity) {
        this.backQuantity = backQuantity;
    }

    public Goods getGoods() {
        return goods;
    }

    public BigDecimal getOnWayQuantity() {
        return onWayQuantity;
    }

    public void setOnWayQuantity(BigDecimal onWayQuantity) {
        this.onWayQuantity = onWayQuantity;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public String getBaseMedName() {
        return baseMedName;
    }

    public void setBaseMedName(String baseMedName) {
        this.baseMedName = baseMedName;
    }

    public String getStorageConditionName() {
        return storageConditionName;
    }

    public void setStorageConditionName(String storageConditionName) {
        this.storageConditionName = storageConditionName;
    }

    public SellOrderDetail clone() throws CloneNotSupportedException {
        return (SellOrderDetail) super.clone();
    }
}