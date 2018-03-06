package com.yiban.erp.entities;

import java.util.Date;

public class Goods {
    private Long id;

    private String name;

    private Integer companyId;

    private Integer categoryId;

    private String fullName;

    private String origin;

    private String jx;

    private String spec;

    private String serial;

    private String code;

    private Boolean enable;

    private Integer factoryId;

    private Integer unit;

    private Integer packUnit;

    private Integer mediumPack;

    private Integer bigPack;

    private String pinyin;

    private String storageCondition;

    private String storageDescription;

    private String comment;

    private Long certId;

    private Integer brandId;

    private Long permitId;

    private Integer jxId;

    private Boolean isForeign;

    private Boolean isTcm;

    private Boolean isPrescription;

    private Boolean inCheck;

    private Boolean firstCheck;

    private Boolean specialManaged;

    private Boolean needCare;

    private Integer warningDays;

    private Date createdTime;

    private Date updatedTime;

    private String createdBy;

    private String updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null ? null : fullName.trim();
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin == null ? null : origin.trim();
    }

    public String getJx() {
        return jx;
    }

    public void setJx(String jx) {
        this.jx = jx == null ? null : jx.trim();
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec == null ? null : spec.trim();
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial == null ? null : serial.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Integer factoryId) {
        this.factoryId = factoryId;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getPackUnit() {
        return packUnit;
    }

    public void setPackUnit(Integer packUnit) {
        this.packUnit = packUnit;
    }

    public Integer getMediumPack() {
        return mediumPack;
    }

    public void setMediumPack(Integer mediumPack) {
        this.mediumPack = mediumPack;
    }

    public Integer getBigPack() {
        return bigPack;
    }

    public void setBigPack(Integer bigPack) {
        this.bigPack = bigPack;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition == null ? null : storageCondition.trim();
    }

    public String getStorageDescription() {
        return storageDescription;
    }

    public void setStorageDescription(String storageDescription) {
        this.storageDescription = storageDescription == null ? null : storageDescription.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public Long getCertId() {
        return certId;
    }

    public void setCertId(Long certId) {
        this.certId = certId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Long getPermitId() {
        return permitId;
    }

    public void setPermitId(Long permitId) {
        this.permitId = permitId;
    }

    public Integer getJxId() {
        return jxId;
    }

    public void setJxId(Integer jxId) {
        this.jxId = jxId;
    }

    public Boolean getIsForeign() {
        return isForeign;
    }

    public void setIsForeign(Boolean isForeign) {
        this.isForeign = isForeign;
    }

    public Boolean getIsTcm() {
        return isTcm;
    }

    public void setIsTcm(Boolean isTcm) {
        this.isTcm = isTcm;
    }

    public Boolean getIsPrescription() {
        return isPrescription;
    }

    public void setIsPrescription(Boolean isPrescription) {
        this.isPrescription = isPrescription;
    }

    public Boolean getInCheck() {
        return inCheck;
    }

    public void setInCheck(Boolean inCheck) {
        this.inCheck = inCheck;
    }

    public Boolean getFirstCheck() {
        return firstCheck;
    }

    public void setFirstCheck(Boolean firstCheck) {
        this.firstCheck = firstCheck;
    }

    public Boolean getSpecialManaged() {
        return specialManaged;
    }

    public void setSpecialManaged(Boolean specialManaged) {
        this.specialManaged = specialManaged;
    }

    public Boolean getNeedCare() {
        return needCare;
    }

    public void setNeedCare(Boolean needCare) {
        this.needCare = needCare;
    }

    public Integer getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Integer warningDays) {
        this.warningDays = warningDays;
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
}