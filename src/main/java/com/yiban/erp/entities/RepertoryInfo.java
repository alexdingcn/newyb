package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryInfo {
    private Long id;
    private Integer companyId;

    private Integer warehouseId;

    private String location;

    private Long inUserId;

    private String batchCode;

    private Long goodsId;

    private Long factoryId;

    private BigDecimal inQuantity;

    private BigDecimal quantity;

    private BigDecimal buyPrice;

    private Boolean isExp;

    private Boolean saleEnable;

    private Date productDate;

    private Date inDate;

    private Date expDate;

    private Long supplierId;

    private Long supplierContactId;

    private Long buyerId;

    private Long orderId; //入库单号

    private Boolean saleSate;

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
    private String in_user_name;
    private String origin;
    private String jx;
    private String spec;
    private String unitName;
    private String med_type;
    private long permit_id;
    private long base_med_id;
    private Byte enable;
    private BigDecimal in_tax;
    private BigDecimal out_tax;
    private String storage_condition;

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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode == null ? null : batchCode.trim();
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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Long getInUserId() {
        return inUserId;
    }

    public void setInUserId(Long inUserId) {
        this.inUserId = inUserId;
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

    public String getIn_user_name() {
        return in_user_name;
    }

    public void setIn_user_name(String in_user_name) {
        this.in_user_name = in_user_name;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public BigDecimal getInQuantity() {
        return inQuantity;
    }

    public void setInQuantity(BigDecimal inQuantity) {
        this.inQuantity = inQuantity;
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

    public Boolean getSaleSate() {
        return saleSate;
    }

    public void setSaleSate(Boolean saleSate) {
        this.saleSate = saleSate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getMed_type() {
        return med_type;
    }

    public void setMed_type(String med_type) {
        this.med_type = med_type;
    }

    public long getPermit_id() {
        return permit_id;
    }

    public void setPermit_id(long permit_id) {
        this.permit_id = permit_id;
    }

    public long getBase_med_id() {
        return base_med_id;
    }

    public void setBase_med_id(long base_med_id) {
        this.base_med_id = base_med_id;
    }

    public Byte getEnable() {
        return enable;
    }

    public void setEnable(Byte enable) {
        this.enable = enable;
    }

    public BigDecimal getIn_tax() {
        return in_tax;
    }

    public void setIn_tax(BigDecimal in_tax) {
        this.in_tax = in_tax;
    }

    public BigDecimal getOut_tax() {
        return out_tax;
    }

    public void setOut_tax(BigDecimal out_tax) {
        this.out_tax = out_tax;
    }

    public String getStorage_condition() {
        return storage_condition;
    }

    public void setStorage_condition(String storage_condition) {
        this.storage_condition = storage_condition;
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