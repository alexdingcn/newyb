package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class SellOrderShip {
    private Long id;

    private Long sellOrderId;

    private Integer shipCompanyId;
    private String shipCompanyName;

    private String shipNumber;

    private Date issuanceDate;

    private String shipAddress;

    private String receiveUserName;
    private String receiveUserPhone;
    private String receiveAddress;

    private Long temperControlId;
    private String temperControlName;

    private Long shipToolId;
    private String shipToolName;

    private Long shipMethod;
    private String shipMethodName;

    private String carNumber;

    private String shipPhone;

    private String driverName;

    private String driverFileNo;

    private BigDecimal shipTemper;

    private BigDecimal shipQuantity;

    private String operator;

    private BigDecimal mileage;

    private Date shipStartTime;

    private Date shipEndTime;

    private String comment;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

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

    public Integer getShipCompanyId() {
        return shipCompanyId;
    }

    public void setShipCompanyId(Integer shipCompanyId) {
        this.shipCompanyId = shipCompanyId;
    }

    public String getShipNumber() {
        return shipNumber;
    }

    public void setShipNumber(String shipNumber) {
        this.shipNumber = shipNumber == null ? null : shipNumber.trim();
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Long getShipToolId() {
        return shipToolId;
    }

    public void setShipToolId(Long shipToolId) {
        this.shipToolId = shipToolId;
    }

    public Long getShipMethod() {
        return shipMethod;
    }

    public void setShipMethod(Long shipMethod) {
        this.shipMethod = shipMethod;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress == null ? null : shipAddress.trim();
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber == null ? null : carNumber.trim();
    }

    public String getShipPhone() {
        return shipPhone;
    }

    public void setShipPhone(String shipPhone) {
        this.shipPhone = shipPhone == null ? null : shipPhone.trim();
    }

    public String getShipCompanyName() {
        return shipCompanyName;
    }

    public void setShipCompanyName(String shipCompanyName) {
        this.shipCompanyName = shipCompanyName;
    }

    public String getShipToolName() {
        return shipToolName;
    }

    public void setShipToolName(String shipToolName) {
        this.shipToolName = shipToolName;
    }

    public String getShipMethodName() {
        return shipMethodName;
    }

    public void setShipMethodName(String shipMethodName) {
        this.shipMethodName = shipMethodName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverFileNo() {
        return driverFileNo;
    }

    public void setDriverFileNo(String driverFileNo) {
        this.driverFileNo = driverFileNo;
    }

    public BigDecimal getShipTemper() {
        return shipTemper;
    }

    public void setShipTemper(BigDecimal shipTemper) {
        this.shipTemper = shipTemper;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public Date getShipStartTime() {
        return shipStartTime;
    }

    public void setShipStartTime(Date shipStartTime) {
        this.shipStartTime = shipStartTime;
    }

    public Date getShipEndTime() {
        return shipEndTime;
    }

    public void setShipEndTime(Date shipEndTime) {
        this.shipEndTime = shipEndTime;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getReceiveUserPhone() {
        return receiveUserPhone;
    }

    public void setReceiveUserPhone(String receiveUserPhone) {
        this.receiveUserPhone = receiveUserPhone;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public Long getTemperControlId() {
        return temperControlId;
    }

    public void setTemperControlId(Long temperControlId) {
        this.temperControlId = temperControlId;
    }

    public String getTemperControlName() {
        return temperControlName;
    }

    public void setTemperControlName(String temperControlName) {
        this.temperControlName = temperControlName;
    }

    public void setShipQuantity(BigDecimal shipQuantity) {
        this.shipQuantity = shipQuantity;
    }

    public BigDecimal getShipQuantity() {
        return shipQuantity;
    }
}