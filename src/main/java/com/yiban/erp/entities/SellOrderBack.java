package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SellOrderBack {
    private Long id;

    private Integer companyId;

    private Boolean haveSellOrder;

    private Long refOrderId;

    private String refOrderNo;

    private String orderNumber;

    private String status;

    private Long customerId;

    private String customerName;

    private Long customerRepId;

    private String customerRepName;

    private Long saleId;

    private String saleName;

    private Integer warehouseId;

    private String warehouseName;

    private Long temperControlId;

    private String backReason;

    private Integer shipCompanyId;

    private String shipCompanyName;

    private String shipNo;

    private Date shipDate;

    private Date receiveDate;

    private Long shipMethod;

    private Long shipTool;

    private String shipCarNo;

    private String shipDriverName;

    private String shipStartAddress;

    private BigDecimal receiveTemper;

    private BigDecimal receiveQuantity;

    private BigDecimal totalQuantity;

    private BigDecimal totalAmount;

    private BigDecimal totalCostAmount;

    private BigDecimal freeAmount;

    private String backSaleUser;

    private Date backSaleTime;

    private String backSaleResult;

    private String backQualityUser;

    private Date backQualityTime;

    private String backQualityResult;

    private Date qualityCheckTime;

    private String qualityCheckUser;

    private Date finalCheckTime;

    private String finalCheckUser;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private List<SellOrderBackDetail> details;

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

    public Boolean getHaveSellOrder() {
        if (haveSellOrder == null) {
            return false;
        }
        return haveSellOrder;
    }

    public void setHaveSellOrder(Boolean haveSellOrder) {
        this.haveSellOrder = haveSellOrder;
    }

    public Long getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }

    public String getRefOrderNo() {
        return refOrderNo;
    }

    public void setRefOrderNo(String refOrderNo) {
        this.refOrderNo = refOrderNo == null ? null : refOrderNo.trim();
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber == null ? null : orderNumber.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName == null ? null : customerName.trim();
    }

    public Long getCustomerRepId() {
        return customerRepId;
    }

    public void setCustomerRepId(Long customerRepId) {
        this.customerRepId = customerRepId;
    }

    public String getCustomerRepName() {
        return customerRepName;
    }

    public void setCustomerRepName(String customerRepName) {
        this.customerRepName = customerRepName == null ? null : customerRepName.trim();
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName == null ? null : saleName.trim();
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName == null ? null : warehouseName.trim();
    }

    public Long getTemperControlId() {
        return temperControlId;
    }

    public void setTemperControlId(Long temperControlId) {
        this.temperControlId = temperControlId;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason == null ? null : backReason.trim();
    }

    public Integer getShipCompanyId() {
        return shipCompanyId;
    }

    public void setShipCompanyId(Integer shipCompanyId) {
        this.shipCompanyId = shipCompanyId;
    }

    public String getShipCompanyName() {
        return shipCompanyName;
    }

    public void setShipCompanyName(String shipCompanyName) {
        this.shipCompanyName = shipCompanyName;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo == null ? null : shipNo.trim();
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Long getShipMethod() {
        return shipMethod;
    }

    public void setShipMethod(Long shipMethod) {
        this.shipMethod = shipMethod;
    }

    public Long getShipTool() {
        return shipTool;
    }

    public void setShipTool(Long shipTool) {
        this.shipTool = shipTool;
    }

    public String getShipCarNo() {
        return shipCarNo;
    }

    public void setShipCarNo(String shipCarNo) {
        this.shipCarNo = shipCarNo == null ? null : shipCarNo.trim();
    }

    public String getShipDriverName() {
        return shipDriverName;
    }

    public void setShipDriverName(String shipDriverName) {
        this.shipDriverName = shipDriverName == null ? null : shipDriverName.trim();
    }

    public String getShipStartAddress() {
        return shipStartAddress;
    }

    public void setShipStartAddress(String shipStartAddress) {
        this.shipStartAddress = shipStartAddress == null ? null : shipStartAddress.trim();
    }

    public BigDecimal getReceiveTemper() {
        return receiveTemper;
    }

    public void setReceiveTemper(BigDecimal receiveTemper) {
        this.receiveTemper = receiveTemper;
    }

    public BigDecimal getReceiveQuantity() {
        return receiveQuantity;
    }

    public void setReceiveQuantity(BigDecimal receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalCostAmount() {
        return totalCostAmount;
    }

    public void setTotalCostAmount(BigDecimal totalCostAmount) {
        this.totalCostAmount = totalCostAmount;
    }

    public BigDecimal getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(BigDecimal freeAmount) {
        this.freeAmount = freeAmount;
    }

    public String getBackSaleUser() {
        return backSaleUser;
    }

    public void setBackSaleUser(String backSaleUser) {
        this.backSaleUser = backSaleUser == null ? null : backSaleUser.trim();
    }

    public Date getBackSaleTime() {
        return backSaleTime;
    }

    public void setBackSaleTime(Date backSaleTime) {
        this.backSaleTime = backSaleTime;
    }

    public String getBackSaleResult() {
        return backSaleResult;
    }

    public void setBackSaleResult(String backSaleResult) {
        this.backSaleResult = backSaleResult == null ? null : backSaleResult.trim();
    }

    public String getBackQualityUser() {
        return backQualityUser;
    }

    public void setBackQualityUser(String backQualityUser) {
        this.backQualityUser = backQualityUser == null ? null : backQualityUser.trim();
    }

    public Date getBackQualityTime() {
        return backQualityTime;
    }

    public void setBackQualityTime(Date backQualityTime) {
        this.backQualityTime = backQualityTime;
    }

    public String getBackQualityResult() {
        return backQualityResult;
    }

    public void setBackQualityResult(String backQualityResult) {
        this.backQualityResult = backQualityResult == null ? null : backQualityResult.trim();
    }

    public Date getQualityCheckTime() {
        return qualityCheckTime;
    }

    public void setQualityCheckTime(Date qualityCheckTime) {
        this.qualityCheckTime = qualityCheckTime;
    }

    public String getQualityCheckUser() {
        return qualityCheckUser;
    }

    public void setQualityCheckUser(String qualityCheckUser) {
        this.qualityCheckUser = qualityCheckUser == null ? null : qualityCheckUser.trim();
    }

    public Date getFinalCheckTime() {
        return finalCheckTime;
    }

    public void setFinalCheckTime(Date finalCheckTime) {
        this.finalCheckTime = finalCheckTime;
    }

    public String getFinalCheckUser() {
        return finalCheckUser;
    }

    public void setFinalCheckUser(String finalCheckUser) {
        this.finalCheckUser = finalCheckUser == null ? null : finalCheckUser.trim();
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

    public List<SellOrderBackDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SellOrderBackDetail> details) {
        this.details = details;
    }
}