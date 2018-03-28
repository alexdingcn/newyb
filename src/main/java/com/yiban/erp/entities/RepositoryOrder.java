package com.yiban.erp.entities;

import com.yiban.erp.constant.RepositoryOrderStatus;

import java.util.Date;
import java.util.List;

public class RepositoryOrder {
    private Long id;

    private Integer companyId;

    private Long buyOrderId;

    private String orderNumber;

    private String refNo;

    private Long supplierId;

    private Long supplierContactId;

    private Integer buyerId;

    private String status;

    private String keyWord;

    private Date receiveDate;

    private Date payDate;

    private String goodBillNo;

    private Long tempControlMethod;

    private Integer receiveTemp;

    private Integer checkTemp;

    private String tempControlStatus;

    private Integer shipCompanyId;

    private Long shipMethod;

    private Long shipTool;

    private Integer shipCarNo;

    private String shipDriverName;

    private Date shipStartDate;

    private Date shipEndDate;

    private String shipEntrustNo;

    private String shipStartAddress;

    private Integer warehouseId;

    private String buyType;

    private Integer term;

    private Integer comeFrom;

    private Long billType;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private List<RepositoryOrderDetail> details;

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

    public Long getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(Long buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
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

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord == null ? null : keyWord.trim();
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getGoodBillNo() {
        return goodBillNo;
    }

    public void setGoodBillNo(String goodBillNo) {
        this.goodBillNo = goodBillNo == null ? null : goodBillNo.trim();
    }

    public Long getTempControlMethod() {
        return tempControlMethod;
    }

    public void setTempControlMethod(Long tempControlMethod) {
        this.tempControlMethod = tempControlMethod;
    }

    public Integer getReceiveTemp() {
        return receiveTemp;
    }

    public void setReceiveTemp(Integer receiveTemp) {
        this.receiveTemp = receiveTemp;
    }

    public Integer getCheckTemp() {
        return checkTemp;
    }

    public void setCheckTemp(Integer checkTemp) {
        this.checkTemp = checkTemp;
    }

    public String getTempControlStatus() {
        return tempControlStatus;
    }

    public void setTempControlStatus(String tempControlStatus) {
        this.tempControlStatus = tempControlStatus == null ? null : tempControlStatus.trim();
    }

    public Integer getShipCompanyId() {
        return shipCompanyId;
    }

    public void setShipCompanyId(Integer shipCompanyId) {
        this.shipCompanyId = shipCompanyId;
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

    public Integer getShipCarNo() {
        return shipCarNo;
    }

    public void setShipCarNo(Integer shipCarNo) {
        this.shipCarNo = shipCarNo;
    }

    public String getShipDriverName() {
        return shipDriverName;
    }

    public void setShipDriverName(String shipDriverName) {
        this.shipDriverName = shipDriverName == null ? null : shipDriverName.trim();
    }

    public Date getShipStartDate() {
        return shipStartDate;
    }

    public void setShipStartDate(Date shipStartDate) {
        this.shipStartDate = shipStartDate;
    }

    public Date getShipEndDate() {
        return shipEndDate;
    }

    public void setShipEndDate(Date shipEndDate) {
        this.shipEndDate = shipEndDate;
    }

    public String getShipEntrustNo() {
        return shipEntrustNo;
    }

    public void setShipEntrustNo(String shipEntrustNo) {
        this.shipEntrustNo = shipEntrustNo == null ? null : shipEntrustNo.trim();
    }

    public String getShipStartAddress() {
        return shipStartAddress;
    }

    public void setShipStartAddress(String shipStartAddress) {
        this.shipStartAddress = shipStartAddress == null ? null : shipStartAddress.trim();
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getBuyType() {
        return buyType;
    }

    public void setBuyType(String buyType) {
        this.buyType = buyType == null ? null : buyType.trim();
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(Integer comeFrom) {
        this.comeFrom = comeFrom;
    }

    public Long getBillType() {
        return billType;
    }

    public void setBillType(Long billType) {
        this.billType = billType;
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

    public List<RepositoryOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RepositoryOrderDetail> details) {
        this.details = details;
    }

    public boolean canUpdateDetail() {
        if (RepositoryOrderStatus.TEMP_STORAGE.name().equalsIgnoreCase(this.status)
                || RepositoryOrderStatus.INIT.name().equalsIgnoreCase(this.status)) {
            return true;
        }
        return false;
    }
}