package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryInfo {
    private Long id;
    private Integer companyId;

    private Integer warehouseId;

    private Long inUserId;

    private Long goodsId;

    private Integer inQuantity;

    private Integer quantity;

    private BigDecimal buyPrice;

    private String batchCode;

    private Boolean isExp;

    private Boolean saleEnable;

    private Date productDate;

    private Date inDate;

    private Date expDate;

    private String location;

    private Long supplierId;

    private Long supplierContactId;

    private Long buyerId;

    private Long orderId; //入库单号

    private String storeState;

    private String counterState;

    private String saleSate;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private Goods goods;


    //展示字段
    private String warehouseName;
    private String goodName;
    private String supplier;
    private String factoryName;
    private String inUserName;
    private String origin;
    private String jx;
    private String spec;
    private String unitName;
    private String medType;
    private long permitId;
    private long baseMedId;
    private Byte enable;
    private BigDecimal inax;
    private BigDecimal outTax;
    private String storageCondition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getInUserId() {
        return inUserId;
    }

    public void setInUserId(Long inUserId) {
        this.inUserId = inUserId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getInQuantity() {
        return inQuantity;
    }

    public void setInQuantity(Integer inQuantity) {
        this.inQuantity = inQuantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Boolean getExp() {
        return isExp;
    }

    public void setExp(Boolean exp) {
        isExp = exp;
    }

    public Boolean getSaleEnable() {
        return saleEnable;
    }

    public void setSaleEnable(Boolean saleEnable) {
        this.saleEnable = saleEnable;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getSupplierContactId() {
        return supplierContactId;
    }

    public void setSupplierContactId(Long supplierContactId) {
        this.supplierContactId = supplierContactId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStoreState() {
        return storeState;
    }

    public void setStoreState(String storeState) {
        this.storeState = storeState;
    }

    public String getCounterState() {
        return counterState;
    }

    public void setCounterState(String counterState) {
        this.counterState = counterState;
    }

    public String getSaleSate() {
        return saleSate;
    }

    public void setSaleSate(String saleSate) {
        this.saleSate = saleSate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getInUserName() {
        return inUserName;
    }

    public void setInUserName(String inUserName) {
        this.inUserName = inUserName;
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

    public String getMedType() {
        return medType;
    }

    public void setMedType(String medType) {
        this.medType = medType;
    }

    public long getPermitId() {
        return permitId;
    }

    public void setPermitId(long permitId) {
        this.permitId = permitId;
    }

    public long getBaseMedId() {
        return baseMedId;
    }

    public void setBaseMedId(long baseMedId) {
        this.baseMedId = baseMedId;
    }

    public Byte getEnable() {
        return enable;
    }

    public void setEnable(Byte enable) {
        this.enable = enable;
    }

    public BigDecimal getInax() {
        return inax;
    }

    public void setInax(BigDecimal inax) {
        this.inax = inax;
    }

    public BigDecimal getOutTax() {
        return outTax;
    }

    public void setOutTax(BigDecimal outTax) {
        this.outTax = outTax;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
        if (goods != null) {
            this.goodName = goods.getName();
            this.factoryName = goods.getFactory();
            this.jx = goods.getJx();
            this.spec = goods.getSpec();
            this.unitName = goods.getUnitName();
        }
    }
}