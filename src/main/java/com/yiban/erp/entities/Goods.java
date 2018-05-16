package com.yiban.erp.entities;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

public class Goods {

    private Long id;

    private Long goodsInfoId;

    private String skuKey;

    private Long specOneId;

    private String specOneName;

    private Long specTwoId;

    private String specTwoName;

    private Long specThreeId;

    private String specThreeName;

    private String barCode;

    private Integer companyId;

    private Integer categoryId;

    private String goodsNo;

    private Long brandId;

    private Long supplierId;

    private String status;

    private String name;

    private String fullName;

    private String origin;

    private Boolean enable;

    private Boolean isProxy;

    private Long factoryId;

    private Long unit;

    private Long packUnit;

    private Long mediumUnit;

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

    private Byte isShebao;

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

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private Long att1Id;

    private String att1Value;

    private Long att2Id;

    private String att2Value;

    private Long att3Id;

    private String att3Value;

    private Long att4Id;

    private String att4Value;

    private Long att5Id;

    private String att5Value;

    private Long att6Id;

    private String att6Value;

    private String tags; //转换为List

    private List<String> tagList;

    private Boolean useSpec;

    private BigDecimal batchPrice;
    private BigDecimal retailPrice;
    private BigDecimal inPrice;

    private String categoryName;
    private String brandName;
    private String supplierName;

    // Option value
    private String unitName;
    private String mediumUnitName;
    private String packUnitName;
    private String storageConditionName;
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
        this.setMediumUnitName(optionMap.get(this.getMediumUnit()) != null ? optionMap.get(this.getMediumUnit()).getValue() : null);
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
        optionIdSet.add(this.getMediumUnit() != null ? this.getMediumUnit() : 0);
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

    public Long getGoodsInfoId() {
        return goodsInfoId;
    }

    public void setGoodsInfoId(Long goodsInfoId) {
        this.goodsInfoId = goodsInfoId;
    }

    public String getSkuKey() {
        return skuKey;
    }

    public void setSkuKey(String skuKey) {
        this.skuKey = skuKey;
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

    public String getSpecOneName() {
        return specOneName;
    }

    public void setSpecOneName(String specOneName) {
        this.specOneName = specOneName;
    }

    public String getSpecTwoName() {
        return specTwoName;
    }

    public void setSpecTwoName(String specTwoName) {
        this.specTwoName = specTwoName;
    }

    public String getSpecThreeName() {
        return specThreeName;
    }

    public void setSpecThreeName(String specThreeName) {
        this.specThreeName = specThreeName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getMediumUnit() {
        return mediumUnit;
    }

    public void setMediumUnit(Long mediumUnit) {
        this.mediumUnit = mediumUnit;
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

    public Byte getIsShebao() {
        return isShebao;
    }

    public void setIsShebao(Byte isShebao) {
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

    public Long getAtt1Id() {
        return att1Id;
    }

    public void setAtt1Id(Long att1Id) {
        this.att1Id = att1Id;
    }

    public String getAtt1Value() {
        return att1Value;
    }

    public void setAtt1Value(String att1Value) {
        this.att1Value = att1Value;
    }

    public Long getAtt2Id() {
        return att2Id;
    }

    public void setAtt2Id(Long att2Id) {
        this.att2Id = att2Id;
    }

    public String getAtt2Value() {
        return att2Value;
    }

    public void setAtt2Value(String att2Value) {
        this.att2Value = att2Value;
    }

    public Long getAtt3Id() {
        return att3Id;
    }

    public void setAtt3Id(Long att3Id) {
        this.att3Id = att3Id;
    }

    public String getAtt3Value() {
        return att3Value;
    }

    public void setAtt3Value(String att3Value) {
        this.att3Value = att3Value;
    }

    public Long getAtt4Id() {
        return att4Id;
    }

    public void setAtt4Id(Long att4Id) {
        this.att4Id = att4Id;
    }

    public String getAtt4Value() {
        return att4Value;
    }

    public void setAtt4Value(String att4Value) {
        this.att4Value = att4Value;
    }

    public Long getAtt5Id() {
        return att5Id;
    }

    public void setAtt5Id(Long att5Id) {
        this.att5Id = att5Id;
    }

    public String getAtt5Value() {
        return att5Value;
    }

    public void setAtt5Value(String att5Value) {
        this.att5Value = att5Value;
    }

    public Long getAtt6Id() {
        return att6Id;
    }

    public void setAtt6Id(Long att6Id) {
        this.att6Id = att6Id;
    }

    public String getAtt6Value() {
        return att6Value;
    }

    public void setAtt6Value(String att6Value) {
        this.att6Value = att6Value;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getTagList() {
        if (StringUtils.isEmpty(this.getTags())) {
            return Collections.emptyList();
        }else {
            return JSON.parseArray(this.getTags(), String.class);
        }
    }

    public void setTagList(List<String> tagList) {
        if (tagList != null || tagList.isEmpty()) {
            this.setTags(null);
        }else {
            this.setTags(JSON.toJSONString(tagList));
        }
        this.tagList = tagList;
    }

    public Boolean getUseSpec() {
        return useSpec;
    }

    public void setUseSpec(Boolean useSpec) {
        this.useSpec = useSpec;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public BigDecimal getBatchPrice() {
        return batchPrice;
    }

    public void setBatchPrice(BigDecimal batchPrice) {
        this.batchPrice = batchPrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public BigDecimal getInPrice() {
        return inPrice;
    }

    public void setInPrice(BigDecimal inPrice) {
        this.inPrice = inPrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getMediumUnitName() {
        return mediumUnitName;
    }

    public void setMediumUnitName(String mediumUnitName) {
        this.mediumUnitName = mediumUnitName;
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