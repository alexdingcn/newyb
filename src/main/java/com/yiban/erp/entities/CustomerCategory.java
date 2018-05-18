package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class CustomerCategory {
    private Integer id;

    private Integer companyId;

    private String name;

    private Integer parentId;

    private Integer sequenceNo;

    private String comment;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private BigDecimal batchDiscount;
    private BigDecimal retailDiscount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getParentId() {
        if (this.parentId == null) {    //格式化获取时，如果没有设置，认为是顶级分类，设置为-1
            return -1;
        }
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public BigDecimal getBatchDiscount() {
        return batchDiscount;
    }

    public void setBatchDiscount(BigDecimal batchDiscount) {
        this.batchDiscount = batchDiscount;
    }

    public BigDecimal getRetailDiscount() {
        return retailDiscount;
    }

    public void setRetailDiscount(BigDecimal retailDiscount) {
        this.retailDiscount = retailDiscount;
    }
}