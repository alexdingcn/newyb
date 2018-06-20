package com.yiban.erp.entities;

import java.util.Date;

public class TodoItems {
    private int id;

    private String content;

    private Date  createTime;

    private String createBy;

    private Date dealWith;

    private Boolean status;

    private int companyId;

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

    public Date getDealWith() {
        return dealWith;
    }

    public void setDealWith(Date dealWith) {
        this.dealWith = dealWith;
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

    @Override
    public String toString() {
        return "TodoItems{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", createBy='" + createBy + '\'' +
                ", dealWith=" + dealWith +
                ", status=" + status +
                ", companyId=" + companyId +
                '}';
    }
}
