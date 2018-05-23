package com.yiban.erp.entities;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Company {
    private Integer id;

    private String name;

    private String license;

    private Boolean enabled;

    private Date createdTime;

    private Date updatedTime;

    private Date expiredTime;

    private String createdBy;

    private String updatedBy;

    private BigDecimal accountAmount; //公司账户余额
    private BigDecimal inAmount; //公司应收余额
    private BigDecimal outAmount; //公司应付余额

    private String industry;

    private String city;

    private List<PlaceCode> placeCodes;

    private String address;

    private String email;

    private String fax;

    private String contactPerson;

    private String contactPhone;

    private String introduce;

    private String accountName;

    private String accountBank;

    private String accountNum;

    private String billing;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license == null ? null : license.trim();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public BigDecimal getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(BigDecimal accountAmount) {
        this.accountAmount = accountAmount;
    }

    public BigDecimal getInAmount() {
        return inAmount;
    }

    public void setInAmount(BigDecimal inAmount) {
        this.inAmount = inAmount;
    }

    public BigDecimal getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(BigDecimal outAmount) {
        this.outAmount = outAmount;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getIndustry() { return industry; }

    public void setIndustry(String industry) { this.industry = industry; }

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

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFax() { return fax; }

    public void setFax(String fax) { this.fax = fax; }

    public String getIntroduce() { return introduce; }

    public void setIntroduce(String introduce) { this.introduce = introduce; }

    public String getBilling() { return billing; }

    public void setBilling(String billing) { this.billing = billing; }

    public String getContactPerson() { return contactPerson; }

    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }

    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAccountName() { return accountName; }

    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountBank() { return accountBank; }

    public void setAccountBank(String accountBank) { this.accountBank = accountBank; }

    public String getAccountNum() { return accountNum; }

    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }
}