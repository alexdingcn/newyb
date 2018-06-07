package com.yiban.erp.entities;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

public class GoodsInfo {
    private Long id;

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
    private Boolean ifConser;

    private Boolean isProxy;

    private Long factoryId;

    private String factoryName;

    private Long unit;

    private Long packUnit;

    private Long mediumUnit;

    private BigDecimal mediumPack;

    private BigDecimal bigPack;

    private String pinyin;

    private Long storageCondition;

    private String comment;

    private String cert1Name;

    private Date cert1ExpDate;

    private String cert1No;

    private String cert2Name;

    private Date cert2ExpDate;

    private String cert2No;

    private String cert3Name;

    private Date cert3ExpDate;

    private String cert3No;

    private String fileNo;

    private Boolean inCheck;

    private Boolean firstCheck;

    private Boolean specialManage;

    private Boolean coldManage;

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

    private List<GoodsAttributeRef> attributeRefs;

    private String tags; //转换为List

    private List<String> tagList;

    private Boolean useSpec;

    private String specDesc; //规格描述，单规格时使用，存在detail表中的

    private String barCode;

    private Integer minOrderLimit;

    private List<GoodsDetail> goodsDetails;

    private BigDecimal batchPrice;
    private BigDecimal retailPrice;
    private BigDecimal inPrice;

    private BigDecimal normalLimit;
    private BigDecimal lowLimit;
    private BigDecimal highLimit;

    //列表辅助显示
    private Integer detailsSize;
    private String categoryName;
    private String brandName;
    private String supplierName;

    private String unitName;
    private String mediumUnitName;
    private String packUnitName;

    private String blackList;

    public void setOptions(List<Options> options) {
        if (options == null || options.isEmpty()) {
            return;
        }
        Map<Long, Options> optionMap = new HashMap<>();
        options.stream().forEach(item -> {
            optionMap.put(item.getId(), item);
        });
        this.setUnitName(optionMap.get(this.getUnit()) != null ? optionMap.get(this.getUnit()).getValue() : null);
        this.setPackUnitName(optionMap.get(this.getPackUnit()) != null ? optionMap.get(this.getPackUnit()).getValue() : null);
        this.setMediumUnitName(optionMap.get(this.getMediumUnit()) != null ? optionMap.get(this.getMediumUnit()).getValue() : null);
    }

    public Set<Long> getOptionIds() {
        Set<Long> optionIds = new HashSet<>();
        optionIds.add(this.getUnit() != null ? this.getUnit() : 0);
        optionIds.add(this.getMediumUnit() != null ? this.getMediumUnit() : 0);
        optionIds.add(this.getPackUnit() != null ? this.getPackUnit() : 0);
        return optionIds;
    }

    public Integer getDetailsSize() {
        return detailsSize;
    }

    public void setDetailsSize(Integer detailsSize) {
        this.detailsSize = detailsSize;
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

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo == null ? null : goodsNo.trim();
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
        this.status = status == null ? null : status.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getIfConser() {
        return ifConser;
    }

    public void setIfConser(Boolean ifConser) {
        this.ifConser = ifConser;
    }

    public Boolean getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(Boolean isProxy) {
        this.isProxy = isProxy;
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
        this.pinyin = pinyin == null ? null : pinyin.trim();
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
        this.comment = comment == null ? null : comment.trim();
    }

    public String getCert1Name() {
        return cert1Name;
    }

    public void setCert1Name(String cert1Name) {
        this.cert1Name = cert1Name;
    }

    public Date getCert1ExpDate() {
        return cert1ExpDate;
    }

    public void setCert1ExpDate(Date cert1ExpDate) {
        this.cert1ExpDate = cert1ExpDate;
    }

    public String getCert1No() {
        return cert1No;
    }

    public void setCert1No(String cert1No) {
        this.cert1No = cert1No;
    }

    public String getCert2Name() {
        return cert2Name;
    }

    public void setCert2Name(String cert2Name) {
        this.cert2Name = cert2Name;
    }

    public Date getCert2ExpDate() {
        return cert2ExpDate;
    }

    public void setCert2ExpDate(Date cert2ExpDate) {
        this.cert2ExpDate = cert2ExpDate;
    }

    public String getCert2No() {
        return cert2No;
    }

    public void setCert2No(String cert2No) {
        this.cert2No = cert2No;
    }

    public String getCert3Name() {
        return cert3Name;
    }

    public void setCert3Name(String cert3Name) {
        this.cert3Name = cert3Name;
    }

    public Date getCert3ExpDate() {
        return cert3ExpDate;
    }

    public void setCert3ExpDate(Date cert3ExpDate) {
        this.cert3ExpDate = cert3ExpDate;
    }

    public String getCert3No() {
        return cert3No;
    }

    public void setCert3No(String cert3No) {
        this.cert3No = cert3No;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
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

    public Boolean getSpecialManage() {
        return specialManage;
    }

    public void setSpecialManage(Boolean specialManage) {
        this.specialManage = specialManage;
    }

    public Boolean getColdManage() {
        return coldManage;
    }

    public void setColdManage(Boolean coldManage) {
        this.coldManage = coldManage;
    }

    public Boolean getNeedCare() {
        return needCare == null ? false: needCare;
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
        this.contact = contact == null ? null : contact.trim();
    }

    public String getCureRange() {
        return cureRange;
    }

    public void setCureRange(String cureRange) {
        this.cureRange = cureRange == null ? null : cureRange.trim();
    }

    public String getSalePolicy() {
        return salePolicy;
    }

    public void setSalePolicy(String salePolicy) {
        this.salePolicy = salePolicy == null ? null : salePolicy.trim();
    }

    public Boolean getIsForeign() {
        return isForeign;
    }

    public void setIsForeign(Boolean isForeign) {
        this.isForeign = isForeign;
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

    public List<GoodsAttributeRef> getAttributeRefs() {
        return attributeRefs;
    }

    public void setAttributeRefs(List<GoodsAttributeRef> attributeRefs) {
        this.attributeRefs = attributeRefs;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags == null ? null : tags.trim();
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

    public List<GoodsDetail> getGoodsDetails() {
        return goodsDetails;
    }

    public void setGoodsDetails(List<GoodsDetail> goodsDetails) {
        this.goodsDetails = goodsDetails;
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

    public BigDecimal getNormalLimit() {
        return normalLimit;
    }

    public void setNormalLimit(BigDecimal normalLimit) {
        this.normalLimit = normalLimit;
    }

    public BigDecimal getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(BigDecimal lowLimit) {
        this.lowLimit = lowLimit;
    }

    public BigDecimal getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(BigDecimal highLimit) {
        this.highLimit = highLimit;
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getSpecDesc() {
        return specDesc;
    }

    public void setSpecDesc(String specDesc) {
        this.specDesc = specDesc;
    }

    public Integer getMinOrderLimit() {
        return minOrderLimit;
    }

    public void setMinOrderLimit(Integer minOrderLimit) {
        this.minOrderLimit = minOrderLimit;
    }
}