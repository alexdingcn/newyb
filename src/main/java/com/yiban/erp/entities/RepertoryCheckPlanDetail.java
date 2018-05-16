package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryCheckPlanDetail {
    private Long id;

    private Long checkPlanId;
    private Long makeUserId;
    private Long formId;
    private String formNo;

    private String formStatus;

    private Long repertoryInfoId;

    private Long goodsId;

    private String batchCode;

    private BigDecimal accLimit;

    private BigDecimal checkLimit;

    private String checkNote;

    private Integer checkStatus;

    private BigDecimal price;

    private BigDecimal amount;

    private Date productDate;

    private Date expDate;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;
    //前端显示字段

    private RepertoryInfo repertoryInfo;
    private String code;
    private String origin;
    private String location;
    private String spec;
    private String jx;
    private String unitName;
    private String goodsName;
    private Long factoryId;
    private String factoryName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCheckPlanId() {
        return checkPlanId;
    }

    public void setCheckPlanId(Long checkPlanId) {
        this.checkPlanId = checkPlanId;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public String getFormNo() {
        return formNo;
    }

    public void setFormNo(String formNo) {
        this.formNo = formNo == null ? null : formNo.trim();
    }

    public String getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(String formStatus) {
        this.formStatus = formStatus == null ? null : formStatus.trim();
    }

    public Long getRepertoryInfoId() {
        return repertoryInfoId;
    }

    public void setRepertoryInfoId(Long repertoryInfoId) {
        this.repertoryInfoId = repertoryInfoId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode == null ? null : batchCode.trim();
    }

    public BigDecimal getAccLimit() {
        return accLimit;
    }

    public void setAccLimit(BigDecimal accLimit) {
        this.accLimit = accLimit;
    }

    public BigDecimal getCheckLimit() {
        return checkLimit;
    }

    public void setCheckLimit(BigDecimal checkLimit) {
        this.checkLimit = checkLimit;
    }

    public String getCheckNote() {
        return checkNote;
    }

    public void setCheckNote(String checkNote) {
        this.checkNote = checkNote == null ? null : checkNote.trim();
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJx() {
        return jx;
    }

    public void setJx(String jx) {
        this.jx = jx;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public RepertoryInfo getRepertoryInfo() {
        return repertoryInfo;
    }

    public Long getMakeUserId() {
        return makeUserId;
    }

    public void setMakeUserId(Long makeUserId) {
        this.makeUserId = makeUserId;
    }

    public void setRepertoryInfo(RepertoryInfo repertoryInfo) {
        this.repertoryInfo = repertoryInfo;
        if (repertoryInfo != null && repertoryInfo.getGoods() != null) {
            Goods goods = repertoryInfo.getGoods();
            this.goodsName = goods.getName();
            this.jx=goods.getJxName();
            this.origin=goods.getOrigin();
            this.unitName=goods.getUnitName();
        }
    }

}