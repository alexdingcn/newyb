package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryInfo {

    private Long id;

    private Integer companyId;

    private Integer warehouseId;

    private String location;

    private Long inUserId;

    private String goodsNo;

    private Long goodsId;

    private String batchCode;

    private Long factoryId;

    private BigDecimal inQuantity;

    private BigDecimal quantity;

    private BigDecimal onWayQuantity;

    private BigDecimal buyPrice;

    private Boolean isExp;

    private Boolean saleEnable;

    private Date productDate;

    private Date expDate;

    private Date inDate;

    private Long supplierId;

    private String supplierName;

    private Long supplierContactId;

    private String supplierContactName;

    private Long buyerId;

    private Long orderId;

    private Long orderDetailId;

    private Boolean saleSate;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    //销售退货入库时登记一些客户的信息
    private Long customerId;
    private Long customerRepId;
    private Long saleId;
    private BigDecimal backPrice;

    //加多两个字段用于关联流水和表明流水的类型
    private String refType;
    private Long refOrderId;


    private Goods goods;

    //展示字段
    private String warehouseName;
    private String goodsName;
    private String factoryName;
    private String origin;
    private String unitName;
    private String baseMedName;
    private String storageConditionName;
    private BigDecimal taxRate;

    public void setGoods(Goods goods) {
        this.goods = goods;
        if (goods != null) {
            this.goodsNo = goods.getGoodsNo();
            this.goodsName = goods.getName();
            this.factoryName=goods.getFactoryName();
            this.origin = goods.getOrigin();
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
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

    public BigDecimal getOnWayQuantity() {
        return onWayQuantity;
    }

    public void setOnWayQuantity(BigDecimal onWayQuantity) {
        this.onWayQuantity = onWayQuantity;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
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

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Boolean getSaleSate() {
        return saleSate;
    }

    public void setSaleSate(Boolean saleSate) {
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerRepId() {
        return customerRepId;
    }

    public void setCustomerRepId(Long customerRepId) {
        this.customerRepId = customerRepId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public BigDecimal getBackPrice() {
        return backPrice;
    }

    public void setBackPrice(BigDecimal backPrice) {
        this.backPrice = backPrice;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public Long getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    @Override
    public String toString() {
        return "RepertoryInfo{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", warehouseId=" + warehouseId +
                ", location='" + location + '\'' +
                ", inUserId=" + inUserId +
                ", goodsNo='" + goodsNo + '\'' +
                ", goodsId=" + goodsId +
                ", batchCode='" + batchCode + '\'' +
                ", factoryId=" + factoryId +
                ", inQuantity=" + inQuantity +
                ", quantity=" + quantity +
                ", onWayQuantity=" + onWayQuantity +
                ", buyPrice=" + buyPrice +
                ", isExp=" + isExp +
                ", saleEnable=" + saleEnable +
                ", productDate=" + productDate +
                ", expDate=" + expDate +
                ", inDate=" + inDate +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", supplierContactId=" + supplierContactId +
                ", supplierContactName='" + supplierContactName + '\'' +
                ", buyerId=" + buyerId +
                ", orderId=" + orderId +
                ", orderDetailId=" + orderDetailId +
                ", saleSate=" + saleSate +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                ", customerId=" + customerId +
                ", customerRepId=" + customerRepId +
                ", saleId=" + saleId +
                ", backPrice=" + backPrice +
                ", refType='" + refType + '\'' +
                ", refOrderId=" + refOrderId +
                ", goods=" + goods +
                ", warehouseName='" + warehouseName + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", factoryName='" + factoryName + '\'' +
                ", origin='" + origin + '\'' +
                ", unitName='" + unitName + '\'' +
                ", baseMedName='" + baseMedName + '\'' +
                ", storageConditionName='" + storageConditionName + '\'' +
                ", taxRate=" + taxRate +
                '}';
    }
}