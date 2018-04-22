package com.yiban.erp.entities;

import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.List;

public class SupplierContact {
    private Long id;

    private Long supplierId;

    private String name;

    private String idcard;

    private Boolean enabled;

    private String phone;

    private String city;

    private List<PlaceCode> placeCodes;

    private String businessScope;

    private String comment;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard == null ? null : idcard.trim();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
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

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getCity() {
        return this.placeCodes == null ? "" : JSON.toJSONString(placeCodes);
    }

    public void setCity(String city) {
        if (city != null) {
            this.city = city.trim();
            this.placeCodes = JSON.parseArray(this.city, PlaceCode.class);
        } else {
            this.city = null;
        }
    }

    public List<PlaceCode> getPlaceCodes() {
        return placeCodes;
    }

    public void setPlaceCodes(List<PlaceCode> placeCodes) {
        this.placeCodes = placeCodes;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}