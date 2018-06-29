package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryOutList {
    private Long id;
    private Long repertoryOutId;
    private int warehouseId;
    private String warehouseName;
    private String location;
    private String goodsName;
    private String origin;
    private String unitName;
    private int quantity;
    private int outAmount;
    private BigDecimal buyPrice;
    private String batchCode;
    private Date expDate;
    private  String refOrderNumber;
    private Date outDate;
    private String comment;
    private String customerName;
    private String goToLocation;
    private int goToWarehouseId;

     private Goods goods;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepertoryOutId() { return repertoryOutId; }

    public void setRepertoryOutId(Long repertoryOutId) { this.repertoryOutId = repertoryOutId; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(int outAmount) {
        this.outAmount = outAmount;
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

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getRefOrderNumber() {
        return refOrderNumber;
    }

    public void setRefOrderNumber(String refOrderNumber) {
        this.refOrderNumber = refOrderNumber;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCustomerName() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getGoToLocation() { return goToLocation;}

    public void setGoToLocation(String goToLocation) { this.goToLocation = goToLocation; }

    public int getGoToWarehouseId() { return goToWarehouseId; }

    public void setGoToWarehouseId(int goToWarehouseId) { this.goToWarehouseId = goToWarehouseId; }

    public Goods getGoods() { return goods; }

    public void setGoods(Goods goods) { this.goods = goods; }

    @Override
    public String toString() {
        return "RepertoryOutList{" +
                "id=" + id +
                ", repertoryOutId=" + repertoryOutId +
                ", warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", location='" + location + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", origin='" + origin + '\'' +
                ", unitName='" + unitName + '\'' +
                ", quantity=" + quantity +
                ", outAmount=" + outAmount +
                ", buyPrice=" + buyPrice +
                ", batchCode='" + batchCode + '\'' +
                ", expDate=" + expDate +
                ", refOrderNumber='" + refOrderNumber + '\'' +
                ", outDate=" + outDate +
                ", comment='" + comment + '\'' +
                ", customerName='" + customerName + '\'' +
                ", goToLocation='" + goToLocation + '\'' +
                ", goToWarehouseId=" + goToWarehouseId +
                ", goods=" + goods +
                '}';
    }
}
