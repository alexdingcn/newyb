package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepositoryOrderDetail {
    private Long id;

    private Long repositoryOrderId;

    private Long goodsId;

    private Integer receiveQuality;

    private Integer bigQuality;

    private Integer free;

    private BigDecimal price;

    private Date expDate;

    private Date productDate;

    private String batchCode;

    private BigDecimal amount;

    private String warehouseLocation;

    private Integer rejectQuality;

    private String rejectComment;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private Goods goods;

    private String goodsName;
    private String origin;
    private String jx;
    private String spec;
    private String factory;
    private String unitName;
    private String packUnitName;
    private Integer bigPack;


    public String getGoodsName() {
        return this.goods == null ? null : this.goods.getName();
    }

    public String getOrigin() {
        return this.goods == null ? null : this.goods.getOrigin();
    }

    public String getJx() {
        return this.goods == null ? null : this.goods.getJx();
    }

    public String getSpec() {
        return this.goods == null ? null : this.goods.getSpec();
    }

    public String getFactory() {
        return this.goods == null ? null : this.goods.getFactory();
    }

    public String getUnitName() {
        return this.goods == null ? null : this.goods.getUnitName();
    }

    public String getPackUnitName() {
        return this.goods == null ? null : this.goods.getPackUnitName();
    }

    public Integer getBigPack() {
        return this.goods == null ? null : this.goods.getBigPack();
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepositoryOrderId() {
        return repositoryOrderId;
    }

    public void setRepositoryOrderId(Long repositoryOrderId) {
        this.repositoryOrderId = repositoryOrderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getReceiveQuality() {
        return receiveQuality;
    }

    public void setReceiveQuality(Integer receiveQuality) {
        this.receiveQuality = receiveQuality;
    }

    public Integer getBigQuality() {
        return bigQuality;
    }

    public void setBigQuality(Integer bigQuality) {
        this.bigQuality = bigQuality;
    }

    public Integer getFree() {
        return free;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode == null ? null : batchCode.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation == null ? null : warehouseLocation.trim();
    }

    public Integer getRejectQuality() {
        return rejectQuality;
    }

    public void setRejectQuality(Integer rejectQuality) {
        this.rejectQuality = rejectQuality;
    }

    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment == null ? null : rejectComment.trim();
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
}