package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryInBackDetail {
    private Long id;

    private Long inBackId;

    private Long repertoryInfoId;

    private Long goodsId;

    private BigDecimal backQuantity;

    private BigDecimal buyPrice;

    private BigDecimal amount;

    private String batchCode;

    private String location;

    private Date expDate;

    private Date productDate;

    private BigDecimal taxRate;

    private Boolean saleState;

    private Boolean checkStatus;

    private String checkUser;

    private String checkResult;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private Long supplierId; //用户创建时验证是否为同一个供应商


    private Goods goods;

    //展示字段
    private String warehouseName;
    private String goodsName;
    private String factoryName;
    private String origin;
    private String jx;
    private String spec;
    private String unitName;
    private String baseMedName;
    private String storageConditionName;

    public void setGoods(Goods goods) {
        this.goods = goods;
        if (goods != null) {
            this.goodsName = goods.getName();
//            this.factoryName = goods.getFactory();
            this.origin = goods.getOrigin();
            this.jx = goods.getJxName();
            this.unitName = goods.getUnitName();
            this.baseMedName = goods.getBaseMedName();
            this.storageConditionName = goods.getStorageConditionName();
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInBackId() {
        return inBackId;
    }

    public void setInBackId(Long inBackId) {
        this.inBackId = inBackId;
    }

    public Long getRepertoryInfoId() {
        return repertoryInfoId;
    }

    public void setRepertoryInfoId(Long repertoryInfoId) {
        this.repertoryInfoId = repertoryInfoId;
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
        this.location = location;
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

    public Boolean getSaleState() {
        return saleState;
    }

    public void setSaleState(Boolean saleState) {
        this.saleState = saleState;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Goods getGoods() {
        return goods;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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
}