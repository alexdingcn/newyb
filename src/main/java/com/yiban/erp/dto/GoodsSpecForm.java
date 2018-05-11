package com.yiban.erp.dto;

import com.yiban.erp.entities.GoodsSpec;

import java.util.List;

public class GoodsSpecForm {

    private Long parentId;

    private String parentName;

    private String parentNo;

    private String subValues;

    private List<GoodsSpec> subGoodsSpecs;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentNo() {
        return parentNo;
    }

    public void setParentNo(String parentNo) {
        this.parentNo = parentNo;
    }

    public String getSubValues() {
        return subValues;
    }

    public void setSubValues(String subValues) {
        this.subValues = subValues;
    }

    public List<GoodsSpec> getSubGoodsSpecs() {
        return subGoodsSpecs;
    }

    public void setSubGoodsSpecs(List<GoodsSpec> subGoodsSpecs) {
        this.subGoodsSpecs = subGoodsSpecs;
    }
}
