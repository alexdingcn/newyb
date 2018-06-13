package com.yiban.erp.entities;

import java.util.Date;

public class Billboard {
    private int id;
    private int number;
    private String title;
    private String content;
    private boolean status;
    private String createBy;
    private Date createTime;
    private int companyId;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getNumber() { return number; }

    public void setNumber(int number) { this.number = number; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public boolean isStatus() { return status; }

    public void setStatus(boolean status) { this.status = status; }

    public String getCreateBy() { return createBy; }

    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }

    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public int getCompanyId() { return companyId; }

    public void setCompanyId(int companyId) { this.companyId = companyId; }

    @Override
    public String toString() {
        return "Billboard{" +
                "id=" + id +
                ", number=" + number +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", companyId=" + companyId +
                '}';
    }
}
