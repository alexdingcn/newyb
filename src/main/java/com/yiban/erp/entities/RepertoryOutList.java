package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryOutList {
    private Long id;
    private int warehouseId;
    private String warehouseName;
    private String location;
    private String goodsName;
    private String origin;
    private String specNameOne;
    private String specNameTwo;
    private String specNameThree;
    private String unitName;
    private int quantity;
    private int outAmount;
    private BigDecimal buyPrice;
    private String batchCode;
    private Date expDate;
    private  String refOrderNumber;
    private Date outDate;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSpecNameOne() {
        return specNameOne;
    }

    public void setSpecNameOne(String specNameOne) {
        this.specNameOne = specNameOne;
    }

    public String getSpecNameTwo() {
        return specNameTwo;
    }

    public void setSpecNameTwo(String specNameTwo) {
        this.specNameTwo = specNameTwo;
    }

    public String getSpecNameThree() {
        return specNameThree;
    }

    public void setSpecNameThree(String specNameThree) {
        this.specNameThree = specNameThree;
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

    @Override
    public String toString() {
        return "RepertoryOutList{" +
                "id=" + id +
                ", warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", location='" + location + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", origin='" + origin + '\'' +
                ", specNameOne='" + specNameOne + '\'' +
                ", specNameTwo='" + specNameTwo + '\'' +
                ", specNameThree='" + specNameThree + '\'' +
                ", unitName='" + unitName + '\'' +
                ", quantity=" + quantity +
                ", outAmount=" + outAmount +
                ", buyPrice=" + buyPrice +
                ", batchCode='" + batchCode + '\'' +
                ", expDate=" + expDate +
                ", refOrderNumber='" + refOrderNumber + '\'' +
                ", outDate=" + outDate +
                ", comment='" + comment + '\'' +
                '}';
    }
}
