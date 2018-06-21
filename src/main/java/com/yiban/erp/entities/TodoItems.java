package com.yiban.erp.entities;

import java.util.Date;

public class TodoItems {
    private int id;

    private String content;

    private Date  createTime;

    private String createBy;

    private Date dealTime;

    private Boolean status;

    private int companyId;

    private long refId;

    private String refType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getDealTime() {
        return dealTime;
    }

    public void setDealTime(Date dealTime) {
        this.dealTime = dealTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public String getRefType() { return refType; }

    public void setRefType(String refType) { this.refType = refType; }

    @Override
    public String toString() {
        return "TodoItems{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", createBy='" + createBy + '\'' +
                ", dealTime=" + dealTime +
                ", status=" + status +
                ", companyId=" + companyId +
                ", refId=" + refId +
                ", refType='" + refType + '\'' +
                '}';
    }
}
