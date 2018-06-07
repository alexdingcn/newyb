package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class SellOrderBackDetail {
    private Long id;

    private Long backOrderId;

    private Long sellDetailId;

    private Long repertoryId; //销售单明细关联的库存ID

    private Long goodsId;

    private BigDecimal backQuantity;

    private BigDecimal rightQuantity;

    private BigDecimal badQuantity;

    private BigDecimal backPrice;

    private BigDecimal amount;

    private BigDecimal costAmount;

    private String batchCode;

    private String location;

    private Date expDate;

    private Date productDate;

    private BigDecimal taxRate;

    private String badPlan;

    private String badReason;

    private Boolean checkStatus;

    private Long checkTempMethod;

    private String checkTempMethodName;

    private Date checkTime;

    private String checkUser;

    private String checkResult;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;


    private Goods goods;

    private BigDecimal saleQuantity; // 原始销售单销售数
    private BigDecimal alreadyBackQuantity; // 原始销售单已退货数量

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
            this.origin = goods.getOrigin();
            this.factoryName = goods.getFactoryName();
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

    public Long getBackOrderId() {
        return backOrderId;
    }

    public void setBackOrderId(Long backOrderId) {
        this.backOrderId = backOrderId;
    }

    public Long getSellDetailId() {
        return sellDetailId;
    }

    public void setSellDetailId(Long sellDetailId) {
        this.sellDetailId = sellDetailId;
    }

    public Long getRepertoryId() {
        return repertoryId;
    }

    public void setRepertoryId(Long repertoryId) {
        this.repertoryId = repertoryId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getBackQuantity() {
        return backQuantity;
    }

    public void setBackQuantity(BigDecimal backQuantity) {
        this.backQuantity = backQuantity;
    }

    public BigDecimal getRightQuantity() {
        return rightQuantity;
    }

    public void setRightQuantity(BigDecimal rightQuantity) {
        this.rightQuantity = rightQuantity;
    }

    public BigDecimal getBadQuantity() {
        return badQuantity;
    }

    public void setBadQuantity(BigDecimal badQuantity) {
        this.badQuantity = badQuantity;
    }

    public BigDecimal getBackPrice() {
        return backPrice;
    }

    public void setBackPrice(BigDecimal backPrice) {
        this.backPrice = backPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode == null ? null : batchCode.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public Boolean getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser == null ? null : checkUser.trim();
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult == null ? null : checkResult.trim();
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

    public Goods getGoods() {
        return goods;
    }

    public BigDecimal getSaleQuantity() {
        return saleQuantity;
    }

    public void setSaleQuantity(BigDecimal saleQuantity) {
        this.saleQuantity = saleQuantity;
    }

    public BigDecimal getAlreadyBackQuantity() {
        return alreadyBackQuantity;
    }

    public void setAlreadyBackQuantity(BigDecimal alreadyBackQuantity) {
        this.alreadyBackQuantity = alreadyBackQuantity;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
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

    public String getBadPlan() {
        return badPlan;
    }

    public void setBadPlan(String badPlan) {
        this.badPlan = badPlan;
    }

    public String getBadReason() {
        return badReason;
    }

    public void setBadReason(String badReason) {
        this.badReason = badReason;
    }

    public Long getCheckTempMethod() {
        return checkTempMethod;
    }

    public void setCheckTempMethod(Long checkTempMethod) {
        this.checkTempMethod = checkTempMethod;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckTempMethodName() {
        return checkTempMethodName;
    }

    public void setCheckTempMethodName(String checkTempMethodName) {
        this.checkTempMethodName = checkTempMethodName;
    }
}