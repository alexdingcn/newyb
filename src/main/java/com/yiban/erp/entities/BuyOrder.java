package com.yiban.erp.entities;

import com.yiban.erp.constant.BuyOrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuyOrder {
    private Long id;

    private String orderNumber;

    private Long supplierId;

    private Long supplierContactId;

    private Long buyerId;

    private String status;

    private Integer shipMethodId;

    private Integer shipToolId;

    private Integer temperControlId;

    private Integer warehouseId;

    private Date eta;

    private String refNo;

    private String comment;

    private Integer companyId;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private String checkBy; //审核人
    private String checkResult; //审核结论
    private Date checkTime; //审核时间

    private List<BuyOrderDetail> details;

    private List<BuyOrderRequest> orderItems;

    private String supplier;

    private String supplierContact;

    private String shipTool;

    private String shipMethod;

    private String temperControl;

    private String warehouse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public void setStatus(BuyOrderStatus status) {
        this.status = status == null ? null : status.name();
    }

    public Integer getShipMethodId() {
        return shipMethodId;
    }

    public void setShipMethodId(Integer shipMethodId) {
        this.shipMethodId = shipMethodId;
    }

    public Integer getShipToolId() {
        return shipToolId;
    }

    public void setShipToolId(Integer shipToolId) {
        this.shipToolId = shipToolId;
    }

    public Integer getTemperControlId() {
        return temperControlId;
    }

    public void setTemperControlId(Integer temperControlId) {
        this.temperControlId = temperControlId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo == null ? null : refNo.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
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

    public String getCheckBy() {
        return checkBy;
    }

    public void setCheckBy(String checkBy) {
        this.checkBy = checkBy;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public List<BuyOrderRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<BuyOrderRequest> orderItems) {
        this.orderItems = orderItems;
    }

    public List<BuyOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<BuyOrderDetail> details) {
        this.details = details;
    }

    public void generateOrderDetails() {
        if (this.orderItems != null) {
            this.details = new ArrayList<>();
            for (BuyOrderRequest req : this.orderItems) {
                BuyOrderDetail detail = new BuyOrderDetail();
                detail.setGoodsId(req.getId());
                detail.setGoodsName(req.getName());
                detail.setQuantity(req.getQuantity());
                detail.setBuyPrice(req.getPrice());
                detail.setAmount(req.getAmount());
                detail.setShippedQuantity(BigDecimal.ZERO);
                detail.setBuyOrderId(this.getId());
                detail.setAlreadyFapiao(false);
                this.details.add(detail);
            }
        }
    }

    public BuyOrderStatus getOrderStatus() {
        if (this.status != null) {
            try {
                return BuyOrderStatus.valueOf(this.status);
            } catch (IllegalArgumentException ex) {
            }
        }
        return BuyOrderStatus.UNKNOWN;
    }

    public boolean canUpdateDetails() {
        if (BuyOrderStatus.INIT.name().equals(this.status) || BuyOrderStatus.CHECKING.name().equals(this.status)) {
            return true;
        }
        return false;
    }

    public String getSupplierContact() {
        return supplierContact;
    }

    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getShipTool() {
        return shipTool;
    }

    public void setShipTool(String shipTool) {
        this.shipTool = shipTool;
    }

    public String getShipMethod() {
        return shipMethod;
    }

    public void setShipMethod(String shipMethod) {
        this.shipMethod = shipMethod;
    }

    public String getTemperControl() {
        return temperControl;
    }

    public void setTemperControl(String temperControl) {
        this.temperControl = temperControl;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
}