package com.yiban.erp.entities;

import com.yiban.erp.constant.RepertoryInStatus;
import com.yiban.erp.constant.RepertoryRefType;

import java.math.BigDecimal;
import java.util.*;

public class RepertoryIn {
    private Long id;

    private Integer companyId;

    private Long refOrderId;

    private String refType;

    private String refTypeName;

    private String orderNumber;

    private String refNo;

    private Long supplierId;

    private Long supplierContactId;

    private Long buyerId;

    private String status;

    private String keyWord;

    private Date receiveDate;

    //收货员(如果商品是配置了特殊经营管理特性时，需要验证供应商需要有对应资质和收货员需要双人收货, 双人收货双人姓名是直接按";"(不区分英文和中午分号)分号切割)
    private String receiveUser;

    private Date payDate;

    private String goodsBillNo;

    private Long tempControlMethod;

    private BigDecimal receiveTemp;

    private BigDecimal checkTemp;

    private Long tempControlStatus;

    private Integer shipCompanyId;

    private Long shipMethod;

    private Long shipTool;

    private String shipCarNo;

    private String shipDriverName;

    private Date shipStartDate;

    private Date shipEndDate;

    private String shipEntrustNo;

    private String shipStartAddress;

    private Integer warehouseId;

    private Long buyType;

    private Integer term;

    private String comeFrom;

    private Long billType;

    private String fileNo;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;

    private String supplierName;
    private Boolean supplierColdManage; // 供应商是否冷链经营
    private Boolean supplierSpecialManage; //供应上是否有药品特殊管理资质

    private String supplierContactName;
    private String warehouseName;
    private String saleNickName;
    private String saleRealName;
    private String shipCompanyName;

    private String tempControlMethodName;
    private String tempControlStatusName;
    private String shipMethodName;
    private String shipToolName;
    private String buyTypeName;
    private String billTypeName;

    private List<RepertoryInDetail> details;

    public boolean canUpdateDetail() {
        if (RepertoryInStatus.TEMP_STORAGE.name().equalsIgnoreCase(this.status)
                || RepertoryInStatus.INIT.name().equalsIgnoreCase(this.status)
                || RepertoryInStatus.CHECKED.name().equalsIgnoreCase(this.status)) {
            return true;
        }
        return false;
    }

    public void setOptionName(List<Options> optionsList) {
        if (optionsList == null || optionsList.isEmpty()) {
            return;
        }
        Map<Long, Options> map = new HashMap<>();
        optionsList.stream().forEach(item -> map.put(item.getId(), item));
        this.tempControlMethodName = map.get(this.getTempControlMethod()) != null ? map.get(this.getTempControlMethod()).getValue() : null;
        this.tempControlStatusName = map.get(this.getTempControlStatus()) != null ? map.get(this.getTempControlStatus()).getValue() : null;
        this.shipMethodName = map.get(this.getShipMethod()) != null ? map.get(this.getShipMethod()).getValue() : null;
        this.shipToolName = map.get(this.getShipTool()) != null ? map.get(this.getShipTool()).getValue() : null;
        this.buyTypeName = map.get(this.getBuyType()) != null ? map.get(this.getBuyType()).getValue() : null;
        this.billTypeName = map.get(this.getBillType()) != null ? map.get(this.getBillType()).getValue() : null;
    }

    public Set<Long> getOptionIdList() {
        Set<Long> optionIdSet = new HashSet<>();
        optionIdSet.add(this.tempControlMethod != null ? this.tempControlMethod : 0);
        optionIdSet.add(this.tempControlStatus != null ? this.tempControlStatus : 0);
        optionIdSet.add(this.shipMethod != null ? this.shipMethod : 0);
        optionIdSet.add(this.shipTool != null ? this.shipTool : 0);
        optionIdSet.add(this.buyType != null ? this.buyType : 0);
        optionIdSet.add(this.billType != null ? this.billType : 0);
        return optionIdSet;
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

    public Long getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(Long refOrderId) {
        this.refOrderId = refOrderId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType == null ? null : refType.trim();
    }

    public String getRefTypeName() {
        RepertoryRefType type = RepertoryRefType.getByName(this.getRefType());
        return type == null ? null : type.getDesc();
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber == null ? null : orderNumber.trim();
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo == null ? null : refNo.trim();
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

    public String getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser) {
        this.receiveUser = receiveUser;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getGoodsBillNo() {
        return goodsBillNo;
    }

    public void setGoodsBillNo(String goodsBillNo) {
        this.goodsBillNo = goodsBillNo == null ? null : goodsBillNo.trim();
    }

    public Long getTempControlMethod() {
        return tempControlMethod;
    }

    public void setTempControlMethod(Long tempControlMethod) {
        this.tempControlMethod = tempControlMethod;
    }

    public BigDecimal getReceiveTemp() {
        return receiveTemp;
    }

    public void setReceiveTemp(BigDecimal receiveTemp) {
        this.receiveTemp = receiveTemp;
    }

    public BigDecimal getCheckTemp() {
        return checkTemp;
    }

    public void setCheckTemp(BigDecimal checkTemp) {
        this.checkTemp = checkTemp;
    }

    public Long getTempControlStatus() {
        return tempControlStatus;
    }

    public void setTempControlStatus(Long tempControlStatus) {
        this.tempControlStatus = tempControlStatus;
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

    public Long getBuyType() {
        return buyType;
    }

    public void setBuyType(Long buyType) {
        this.buyType = buyType;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom == null ? null : comeFrom.trim();
    }

    public Long getBillType() {
        return billType;
    }

    public void setBillType(Long billType) {
        this.billType = billType;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo == null ? null : fileNo.trim();
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Boolean getSupplierColdManage() {
        return supplierColdManage;
    }

    public void setSupplierColdManage(Boolean supplierColdManage) {
        this.supplierColdManage = supplierColdManage;
    }

    public Boolean getSupplierSpecialManage() {
        return supplierSpecialManage;
    }

    public void setSupplierSpecialManage(Boolean supplierSpecialManage) {
        this.supplierSpecialManage = supplierSpecialManage;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getSaleNickName() {
        return saleNickName;
    }

    public void setSaleNickName(String saleNickName) {
        this.saleNickName = saleNickName;
    }

    public String getSaleRealName() {
        return saleRealName;
    }

    public void setSaleRealName(String saleRealName) {
        this.saleRealName = saleRealName;
    }

    public String getTempControlMethodName() {
        return tempControlMethodName;
    }

    public void setTempControlMethodName(String tempControlMethodName) {
        this.tempControlMethodName = tempControlMethodName;
    }

    public String getTempControlStatusName() {
        return tempControlStatusName;
    }

    public void setTempControlStatusName(String tempControlStatusName) {
        this.tempControlStatusName = tempControlStatusName;
    }

    public String getShipMethodName() {
        return shipMethodName;
    }

    public void setShipMethodName(String shipMethodName) {
        this.shipMethodName = shipMethodName;
    }

    public String getShipToolName() {
        return shipToolName;
    }

    public void setShipToolName(String shipToolName) {
        this.shipToolName = shipToolName;
    }

    public String getBuyTypeName() {
        return buyTypeName;
    }

    public void setBuyTypeName(String buyTypeName) {
        this.buyTypeName = buyTypeName;
    }

    public String getBillTypeName() {
        return billTypeName;
    }

    public void setBillTypeName(String billTypeName) {
        this.billTypeName = billTypeName;
    }

    public List<RepertoryInDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RepertoryInDetail> details) {
        this.details = details;
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
}