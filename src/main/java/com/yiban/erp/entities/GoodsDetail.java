package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoodsDetail {
    private Long id;

    private Integer companyId;

    private Long goodsInfoId;

    private String status;

    private String skuKey;

    private Long specOneId;
    private String specOneName;

    private Long specTwoId;
    private String specTwoName;

    private Long specThreeId;
    private String specThreeName;

    private String specDesc;

    private String barCode;

    private BigDecimal retailPrice;

    private BigDecimal batchPrice;

    private BigDecimal inPrice;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private Date lastUsedTime; //最后一次使用时间
    private Integer usedCount; //使用次数

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

    public Long getGoodsInfoId() {
        return goodsInfoId;
    }

    public void setGoodsInfoId(Long goodsInfoId) {
        this.goodsInfoId = goodsInfoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getSkuKey() {
        return skuKey;
    }

    public void setSkuKey(String skuKey) {
        this.skuKey = skuKey == null ? null : skuKey.trim();
    }

    public Long getSpecOneId() {
        return specOneId;
    }

    public void setSpecOneId(Long specOneId) {
        this.specOneId = specOneId;
    }

    public Long getSpecTwoId() {
        return specTwoId;
    }

    public void setSpecTwoId(Long specTwoId) {
        this.specTwoId = specTwoId;
    }

    public Long getSpecThreeId() {
        return specThreeId;
    }

    public void setSpecThreeId(Long specThreeId) {
        this.specThreeId = specThreeId;
    }

    public String getSpecDesc() {
        StringBuilder sb = new StringBuilder();
        if (this.specOneId != null) {
            sb.append(this.specOneName).append(";");
        }
        if (this.specTwoId != null) {
            sb.append(this.specTwoName).append(";");
        }
        if (this.specThreeId != null) {
            sb.append(this.specThreeName);
        }

        return sb.toString();
    }

    public void setSpecDesc(String specDesc) {
        this.specDesc = specDesc == null ? null : specDesc.trim();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public BigDecimal getBatchPrice() {
        return batchPrice;
    }

    public void setBatchPrice(BigDecimal batchPrice) {
        this.batchPrice = batchPrice;
    }

    public BigDecimal getInPrice() {
        return inPrice;
    }

    public void setInPrice(BigDecimal inPrice) {
        this.inPrice = inPrice;
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

    public Date getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(Date lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public List<GoodsSpec> getGoodsSpecs() {
        List<GoodsSpec> result = new ArrayList<>();
        GoodsSpec spec;
        if (this.specOneId != null) {
            spec = new GoodsSpec();
            spec.setId(this.specOneId);
            spec.setSpecName(this.specOneName);
            result.add(spec);
        }
        if (this.specTwoId != null) {
            spec = new GoodsSpec();
            spec.setId(this.specTwoId);
            spec.setSpecName(this.specTwoName);
            result.add(spec);
        }
        if (this.specThreeId != null) {
            spec = new GoodsSpec();
            spec.setId(this.specThreeId);
            spec.setSpecName(this.specThreeName);
            result.add(spec);
        }
        return result;
    }

}