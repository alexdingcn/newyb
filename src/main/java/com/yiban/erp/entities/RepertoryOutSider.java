package com.yiban.erp.entities;

import java.util.Date;

public class RepertoryOutSider {
    private Long id;
    private String status;
    private String orderNumber;
    private Date createdTime;
    private String createdBy;
    private String reviewOrderResult;
    private String reviewOrderUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getReviewOrderResult() {
        return reviewOrderResult;
    }

    public void setReviewOrderResult(String reviewOrderResult) {
        this.reviewOrderResult = reviewOrderResult;
    }

    public String getReviewOrderUser() {
        return reviewOrderUser;
    }

    public void setReviewOrderUser(String reviewOrderUser) {
        this.reviewOrderUser = reviewOrderUser;
    }

    @Override
    public String toString() {
        return "RepertoryOutSider{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", createdTime=" + createdTime +
                ", createdBy='" + createdBy + '\'' +
                ", reviewOrderResult='" + reviewOrderResult + '\'' +
                ", reviewOrderUser='" + reviewOrderUser + '\'' +
                '}';
    }
}
