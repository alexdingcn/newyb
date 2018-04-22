package com.yiban.erp.entities;

import com.yiban.erp.constant.OptionsType;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Goods {

    private Long id;

    private Integer companyId;

    private Integer categoryId;

    private String categoryName;

    private String name;

    private String fullName;

    private String origin;

    private String spec;

    private String serial;

    private Boolean enable;

    private Boolean isProxy;

    private Long factoryId;

    private String factory;

    private Long unit;

    private Long packUnit;

    private BigDecimal mediumPack;

    private BigDecimal bigPack;

    private String pinyin;

    private Long storageCondition;

    private String comment;

    private String certNo;

    private Date certExpDate;

    private String certFileNo;

    private String brandNo;

    private Date brandExpDate;

    private String brandFileNo;

    private String permitNo;

    private Date permitExpDate;

    private String permitFileNo;

    private String archiveNo;

    private Boolean inCheck;

    private Boolean firstCheck;

    private Boolean specialManaged;

    private Boolean needCare;

    private BigDecimal inTax;

    private BigDecimal outTax;

    private Integer validMonths;

    private Integer warningDays;

    private String contact;

    private String cureRange;

    private String salePolicy;

    private Boolean isForeign;

    private Long prescriptionId;

    private Long medTypeId;

    private Boolean isShebao;

    private Long specificMedId;

    private Long jxId;

    private Long baseMedId;

    private Long funcCatId;

    private Long medicationId;

    private Long careTimeId;

    private Long gmpTypeId;

    private Long abcTypeId;

    private Long scopeId;

    private Long newTypeId;

    private BigDecimal retailPrice;

    private BigDecimal batchPrice;

    private BigDecimal memberPrice;

    private BigDecimal onlinePrice;

    private BigDecimal splitPrice;

    private BigDecimal lowPrice;

    private BigDecimal hightPrice;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    // Option value
    private String storageConditionName;
    private String unitName;
    private String packUnitName;
    private String specificMedName;
    private String jxName;
    private String baseMedName;
    private String funcCatName;
    private String medicationName;
    private String careTimeName;
    private String gmpTypeName;
    private String abcTypeName;
    private String scopeName;
    private String newTypeName;
    private String prescriptionName; //处方/非处方属性
    private String medTypeName; //中西药属性


    public void setOptionName(List<Options> options) {
        if (options == null || options.isEmpty()) {
            return;
        }
        Map<Long, Options> optionMap = new HashMap<>();
        options.stream().forEach(item -> {
            optionMap.put(item.getId(), item);
        });
        this.setStorageConditionName(optionMap.get(this.getStorageCondition()) != null ? optionMap.get(this.getStorageCondition()).getValue() : null);
        this.setUnitName(optionMap.get(this.getUnit()) != null ? optionMap.get(this.getUnit()).getValue() : null);
        this.setPackUnitName(optionMap.get(this.getPackUnit()) != null ? optionMap.get(this.getPackUnit()).getValue() : null);
        this.setSpecificMedName(optionMap.get(this.getSpecificMedId()) != null ? optionMap.get(this.getSpecificMedId()).getValue() : null);
        this.setJxName(optionMap.get(this.getJxId()) != null ? optionMap.get(this.getJxId()).getValue() : null);
        this.setBaseMedName(optionMap.get(this.getBaseMedId()) != null ? optionMap.get(this.getBaseMedId()).getValue() : null);
        this.setFuncCatName(optionMap.get(this.getFuncCatId()) != null ? optionMap.get(this.getFuncCatId()).getValue() : null);
        this.setMedicationName(optionMap.get(this.getMedicationId()) != null ? optionMap.get(this.getMedicationId()).getValue() : null);
        this.setCareTimeName(optionMap.get(this.getCareTimeId()) != null ? optionMap.get(this.getCareTimeId()).getValue() : null);
        this.setGmpTypeName(optionMap.get(this.getGmpTypeId()) != null ? optionMap.get(this.getCareTimeId()).getValue() : null);
        this.setAbcTypeName(optionMap.get(this.getAbcTypeId()) != null ? optionMap.get(this.getAbcTypeId()).getValue() : null);
        this.setScopeName(optionMap.get(this.getScopeId()) != null ? optionMap.get(this.getScopeId()).getValue() : null);
        this.setNewTypeName(optionMap.get(this.getNewTypeId()) != null ? optionMap.get(this.getNewTypeId()).getValue() : null);
        this.setPrescriptionName(optionMap.get(this.getPrescriptionId()) != null ? optionMap.get(this.getPrescriptionId()).getValue() : null);
        this.setMedTypeName(optionMap.get(this.getMedTypeId()) != null ? optionMap.get(this.getMedTypeId()).getValue() : null);
    }

    public Set<Long> getOptionIdList() {
        Set<Long> optionIdSet = new HashSet<>();
        optionIdSet.add(this.getStorageCondition() != null ? this.getStorageCondition() : 0);
        optionIdSet.add(this.getUnit() != null ? this.getUnit() : 0);
        optionIdSet.add(this.getPackUnit() != null ? this.getPackUnit() : 0);
        optionIdSet.add(this.getSpecificMedId() != null ? this.getSpecificMedId() : 0);
        optionIdSet.add(this.getJxId() != null ? this.getJxId() : 0);
        optionIdSet.add(this.getBaseMedId() != null ? this.getBaseMedId() : 0);
        optionIdSet.add(this.getFuncCatId() != null ? this.getFuncCatId() : 0);
        optionIdSet.add(this.getMedicationId() != null ? this.getMedicationId() : 0);
        optionIdSet.add(this.getCareTimeId() != null ? this.getCareTimeId() : 0);
        optionIdSet.add(this.getGmpTypeId() != null ? this.getGmpTypeId() : 0);
        optionIdSet.add(this.getAbcTypeId() != null ? this.getAbcTypeId() : 0);
        optionIdSet.add(this.getScopeId() != null ? this.getScopeId() : 0);
        optionIdSet.add(this.getNewTypeId() != null ? this.getNewTypeId() : 0);
        optionIdSet.add(this.getPrescriptionId() != null ? this.getPrescriptionId() : 0);
        optionIdSet.add(this.getMedTypeId() != null ? this.getMedTypeId() : 0);

        return optionIdSet;
    }

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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getProxy() {
        return isProxy;
    }

    public void setProxy(Boolean proxy) {
        isProxy = proxy;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Long getPackUnit() {
        return packUnit;
    }

    public void setPackUnit(Long packUnit) {
        this.packUnit = packUnit;
    }

    public BigDecimal getMediumPack() {
        return mediumPack;
    }

    public void setMediumPack(BigDecimal mediumPack) {
        this.mediumPack = mediumPack;
    }

    public BigDecimal getBigPack() {
        return bigPack;
    }

    public void setBigPack(BigDecimal bigPack) {
        this.bigPack = bigPack;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Long getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(Long storageCondition) {
        this.storageCondition = storageCondition;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public Date getCertExpDate() {
        return certExpDate;
    }

    public void setCertExpDate(Date certExpDate) {
        this.certExpDate = certExpDate;
    }

    public String getCertFileNo() {
        return certFileNo;
    }

    public void setCertFileNo(String certFileNo) {
        this.certFileNo = certFileNo;
    }

    public String getBrandNo() {
        return brandNo;
    }

    public void setBrandNo(String brandNo) {
        this.brandNo = brandNo;
    }

    public Date getBrandExpDate() {
        return brandExpDate;
    }

    public void setBrandExpDate(Date brandExpDate) {
        this.brandExpDate = brandExpDate;
    }

    public String getBrandFileNo() {
        return brandFileNo;
    }

    public void setBrandFileNo(String brandFileNo) {
        this.brandFileNo = brandFileNo;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public Date getPermitExpDate() {
        return permitExpDate;
    }

    public void setPermitExpDate(Date permitExpDate) {
        this.permitExpDate = permitExpDate;
    }

    public String getPermitFileNo() {
        return permitFileNo;
    }

    public void setPermitFileNo(String permitFileNo) {
        this.permitFileNo = permitFileNo;
    }

    public String getArchiveNo() {
        return archiveNo;
    }

    public void setArchiveNo(String archiveNo) {
        this.archiveNo = archiveNo;
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

    public BigDecimal getInTax() {
        return inTax;
    }

    public void setInTax(BigDecimal inTax) {
        this.inTax = inTax;
    }

    public BigDecimal getOutTax() {
        return outTax;
    }

    public void setOutTax(BigDecimal outTax) {
        this.outTax = outTax;
    }

    public Integer getValidMonths() {
        return validMonths;
    }

    public void setValidMonths(Integer validMonths) {
        this.validMonths = validMonths;
    }

    public Integer getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Integer warningDays) {
        this.warningDays = warningDays;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCureRange() {
        return cureRange;
    }

    public void setCureRange(String cureRange) {
        this.cureRange = cureRange;
    }

    public String getSalePolicy() {
        return salePolicy;
    }

    public void setSalePolicy(String salePolicy) {
        this.salePolicy = salePolicy;
    }

    public Boolean getForeign() {
        return isForeign;
    }

    public void setForeign(Boolean foreign) {
        isForeign = foreign;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getMedTypeId() {
        return medTypeId;
    }

    public void setMedTypeId(Long medTypeId) {
        this.medTypeId = medTypeId;
    }

    public Boolean getIsShebao() {
        return isShebao;
    }

    public void setIsShebao(Boolean isShebao) {
        this.isShebao = isShebao;
    }

    public Long getSpecificMedId() {
        return specificMedId;
    }

    public void setSpecificMedId(Long specificMedId) {
        this.specificMedId = specificMedId;
    }

    public Long getJxId() {
        return jxId;
    }

    public void setJxId(Long jxId) {
        this.jxId = jxId;
    }

    public Long getBaseMedId() {
        return baseMedId;
    }

    public void setBaseMedId(Long baseMedId) {
        this.baseMedId = baseMedId;
    }

    public Long getFuncCatId() {
        return funcCatId;
    }

    public void setFuncCatId(Long funcCatId) {
        this.funcCatId = funcCatId;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public Long getCareTimeId() {
        return careTimeId;
    }

    public void setCareTimeId(Long careTimeId) {
        this.careTimeId = careTimeId;
    }

    public Long getGmpTypeId() {
        return gmpTypeId;
    }

    public void setGmpTypeId(Long gmpTypeId) {
        this.gmpTypeId = gmpTypeId;
    }

    public Long getAbcTypeId() {
        return abcTypeId;
    }

    public void setAbcTypeId(Long abcTypeId) {
        this.abcTypeId = abcTypeId;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    public Long getNewTypeId() {
        return newTypeId;
    }

    public void setNewTypeId(Long newTypeId) {
        this.newTypeId = newTypeId;
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

    public BigDecimal getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(BigDecimal memberPrice) {
        this.memberPrice = memberPrice;
    }

    public BigDecimal getOnlinePrice() {
        return onlinePrice;
    }

    public void setOnlinePrice(BigDecimal onlinePrice) {
        this.onlinePrice = onlinePrice;
    }

    public BigDecimal getSplitPrice() {
        return splitPrice;
    }

    public void setSplitPrice(BigDecimal splitPrice) {
        this.splitPrice = splitPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getHightPrice() {
        return hightPrice;
    }

    public void setHightPrice(BigDecimal hightPrice) {
        this.hightPrice = hightPrice;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

    public String getStorageConditionName() {
        return storageConditionName;
    }

    public void setStorageConditionName(String storageConditionName) {
        this.storageConditionName = storageConditionName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getPackUnitName() {
        return packUnitName;
    }

    public void setPackUnitName(String packUnitName) {
        this.packUnitName = packUnitName;
    }

    public String getSpecificMedName() {
        return specificMedName;
    }

    public void setSpecificMedName(String specificMedName) {
        this.specificMedName = specificMedName;
    }

    public String getJxName() {
        return jxName;
    }

    public void setJxName(String jxName) {
        this.jxName = jxName;
    }

    public String getBaseMedName() {
        return baseMedName;
    }

    public void setBaseMedName(String baseMedName) {
        this.baseMedName = baseMedName;
    }

    public String getFuncCatName() {
        return funcCatName;
    }

    public void setFuncCatName(String funcCatName) {
        this.funcCatName = funcCatName;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getCareTimeName() {
        return careTimeName;
    }

    public void setCareTimeName(String careTimeName) {
        this.careTimeName = careTimeName;
    }

    public String getGmpTypeName() {
        return gmpTypeName;
    }

    public void setGmpTypeName(String gmpTypeName) {
        this.gmpTypeName = gmpTypeName;
    }

    public String getAbcTypeName() {
        return abcTypeName;
    }

    public void setAbcTypeName(String abcTypeName) {
        this.abcTypeName = abcTypeName;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public String getNewTypeName() {
        return newTypeName;
    }

    public void setNewTypeName(String newTypeName) {
        this.newTypeName = newTypeName;
    }

    public String getPrescriptionName() {
        return prescriptionName;
    }

    public void setPrescriptionName(String prescriptionName) {
        this.prescriptionName = prescriptionName;
    }

    public String getMedTypeName() {
        return medTypeName;
    }

    public void setMedTypeName(String medTypeName) {
        this.medTypeName = medTypeName;
    }
}