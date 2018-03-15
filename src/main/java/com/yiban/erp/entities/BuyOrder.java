package com.yiban.erp.entities;

import com.yiban.erp.constant.BuyOrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuyOrder {
    private Long id;

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

    private List<BuyOrderDetail> details;

    private List<BuyOrderRequest> orderItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                detail.setUnitId(req.getUnit());
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
}