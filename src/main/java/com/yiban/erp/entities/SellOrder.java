package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.*;

public class SellOrder {
    private Long id;

    private Integer companyId;

    private String orderNumber;

    private String refNo;

    private String status;

    private String billStatus;

    private Integer billType;

    private BigDecimal taxRate;

    private String  invoiceTitle;

    private Long customerId;

    private Integer customerRepId;

    private Long saleId;
    private String saleNickName;
    private String saleRealName;

    private Long temperControlId;
    private String temperControlName;

    private Date createOrderDate;

    private String takeGoodsUser;

    private Date payOrderDate;

    private BigDecimal markUpRate;

    private Long shipMethod;
    private String shipMethodName;

    private Long shipTool;
    private String shipToolName;

    private Integer shipCompanyId;

    private String comment;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private Integer warehouseId;

    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
    private BigDecimal freeAmount; //免零金额
    private BigDecimal disRate; //整单折扣率
    private BigDecimal paidAmount;  //已付金额

    private String warehouseName;

    private Customer customer;

    private CustomerRep customerRep;

    private List<SellOrderDetail> details;

    private String customerName;
    private String customerRepName;
    private String customerRepContactPhone;
    private String customerRepRepertoryAddress;


    public Set<Long> getOptionsIds() {
        Set<Long> set = new HashSet<>();
        set.add(this.shipMethod != null ? this.shipMethod : 0L);
        set.add(this.shipTool != null ? this.shipTool : 0L);
        set.add(this.temperControlId != null ? this.temperControlId : 0L);
        return set;
    }

    public void setOptionsName(List<Options> options) {
        if (options == null || options.isEmpty()) {
            return;
        }
        Map<Long, Options> optionsMap = new HashMap<>();
        options.stream().forEach(item -> optionsMap.put(item.getId(), item));
        this.setTemperControlName(optionsMap.get(this.temperControlId) != null ? optionsMap.get(this.temperControlId).getValue() : null);
        this.setShipMethodName(optionsMap.get(this.shipMethod) != null ? optionsMap.get(this.shipMethod).getValue() : null);
        this.setShipToolName(optionsMap.get(this.shipTool) != null ? optionsMap.get(this.shipTool).getValue() : null);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getCustomerRepId() {
        return customerRepId;
    }

    public void setCustomerRepId(Integer customerRepId) {
        this.customerRepId = customerRepId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
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

    public Long getTemperControlId() {
        return temperControlId;
    }

    public void setTemperControlId(Long temperControlId) {
        this.temperControlId = temperControlId;
    }

    public Date getCreateOrderDate() {
        return createOrderDate;
    }

    public void setCreateOrderDate(Date createOrderDate) {
        this.createOrderDate = createOrderDate;
    }

    public String getTakeGoodsUser() {
        return takeGoodsUser;
    }

    public void setTakeGoodsUser(String takeGoodsUser) {
        this.takeGoodsUser = takeGoodsUser;
    }

    public Date getPayOrderDate() {
        return payOrderDate;
    }

    public void setPayOrderDate(Date payOrderDate) {
        this.payOrderDate = payOrderDate;
    }

    public BigDecimal getMarkUpRate() {
        return markUpRate;
    }

    public void setMarkUpRate(BigDecimal markUpRate) {
        this.markUpRate = markUpRate;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
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
        this.warehouseName = warehouseName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerRep getCustomerRep() {
        return customerRep;
    }

    public void setCustomerRep(CustomerRep customerRep) {
        this.customerRep = customerRep;
    }

    public String getCustomerName() {
        if (customer != null) {
            return customer.getName();
        }
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerRepName() {
        if (customerRep != null) {
            return customerRep.getName();
        }
        return customerRepName;
    }

    public void setCustomerRepName(String customerRepName) {
        this.customerRepName = customerRepName;
    }

    public String getCustomerRepContactPhone() {
        if (customerRep != null) {
            return customerRep.getContactPhone();
        }
        return customerRepContactPhone;
    }

    public void setCustomerRepContactPhone(String customerRepContactPhone) {
        this.customerRepContactPhone = customerRepContactPhone;
    }

    public String getCustomerRepRepertoryAddress() {
        if (customerRep != null) {
            return customerRep.getRepertoryAddress();
        }
        return customerRepRepertoryAddress;
    }

    public void setCustomerRepRepertoryAddress(String customerRepRepertoryAddress) {
        this.customerRepRepertoryAddress = customerRepRepertoryAddress;
    }

    public List<SellOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SellOrderDetail> details) {
        this.details = details;
    }

    public String getTemperControlName() {
        return temperControlName;
    }

    public void setTemperControlName(String temperControlName) {
        this.temperControlName = temperControlName;
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

    public Integer getShipCompanyId() {
        return shipCompanyId;
    }

    public void setShipCompanyId(Integer shipCompanyId) {
        this.shipCompanyId = shipCompanyId;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(BigDecimal freeAmount) {
        this.freeAmount = freeAmount;
    }

    public BigDecimal getDisRate() {
        return disRate;
    }

    public void setDisRate(BigDecimal disRate) {
        this.disRate = disRate;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount != null ? paidAmount : BigDecimal.ZERO;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }
}