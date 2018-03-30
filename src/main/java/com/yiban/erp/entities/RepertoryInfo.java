package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryInfo {
    private Long id;
    private Integer company_id;

    private Integer warehouseId;

    private Integer in_user_id;

    private Long goodId;

    private Integer inQuantity;

    private Integer quantity;

    private BigDecimal buyPrice;

    private BigDecimal salePrice;

    private String code;

    private String batchCode;

    private Byte isExp;

    private Byte saleEnable;

    private Date productDate;

    private Date expDate;

    private String location;

    private long supplier_id;

    private long factory_id;

    private Integer store_state;

    private Integer counter_state;

    private Integer sale_sate;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private Goods goods;

    private String warehouseName;
    private String goodName;
    private String factoryName;
    private String in_user_name;
    private String jx;
    private String spec;
    private String unitName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
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

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode == null ? null : batchCode.trim();
    }

    public Byte getIsExp() {
        return isExp;
    }

    public void setIsExp(Byte isExp) {
        this.isExp = isExp;
    }

    public Byte getSaleEnable() {
        return saleEnable;
    }

    public void setSaleEnable(Byte saleEnable) {
        this.saleEnable = saleEnable;
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
        this.location = location == null ? null : location.trim();
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
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
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

    public Goods getGoods() {
        return goods;
    }


    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public Integer getIn_user_id() {
        return in_user_id;
    }

    public void setIn_user_id(Integer in_user_id) {
        this.in_user_id = in_user_id;
    }

    public long getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(long supplier_id) {
        this.supplier_id = supplier_id;
    }

    public long getFactory_id() {
        return factory_id;
    }

    public void setFactory_id(long factory_id) {
        this.factory_id = factory_id;
    }

    public Integer getStore_state() {
        return store_state;
    }

    public void setStore_state(Integer store_state) {
        this.store_state = store_state;
    }

    public Integer getCounter_state() {
        return counter_state;
    }

    public void setCounter_state(Integer counter_state) {
        this.counter_state = counter_state;
    }

    public Integer getSale_sate() {
        return sale_sate;
    }

    public void setSale_sate(Integer sale_sate) {
        this.sale_sate = sale_sate;
    }

    public String getIn_user_name() {
        return in_user_name;
    }

    public void setIn_user_name(String in_user_name) {
        this.in_user_name = in_user_name;
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