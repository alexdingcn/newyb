package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class Goods {
    private Long id;

    private String name;

    private Integer companyId;

    private Integer categoryId;

    private String categoryName;

    private String fullName;

    private String origin;

    private String jx;

    private String spec;

    private String serial;

    private String code;

    private Boolean enable;

    private Long factoryId;

    private String factory;

    private Integer unit;

    private String unitName;

    private Integer packUnit;

    private String packUnitName;

    private Integer mediumPack;

    private Integer bigPack;

    private String pinyin;

    private String storageCondition;

    private String storageDescription;

    private String comment;

    // 注册证
    private Long certId;

    // 商标
    private Integer brandId;

    // 批准文号
    private Long permitId;

    // 剂型属性
    private Integer jxId;

    // 进口/国产
    private Boolean isForeign;

    // 中西药属性
    private Integer medType;

    // 处方/非处方
    private Boolean isPrescription;

    // 入库验收
    private Boolean inCheck;

    // 社保目录内
    private Boolean isShebao;

    private Boolean firstCheck;

    private Boolean specialManaged;

    private Boolean needCare;

    // 预警天数
    private Integer warningDays;
    // 进项税率16%
    private BigDecimal inTax;
    // 销项税率16%
    private BigDecimal outTax;
    // 有效月份
    private Integer validMonths;
    // 联系方式
    private String contact;
    // 档案号
    private String archiveNumber;
    // 主治功能
    private String cureRange;
    // 销售策略
    private String salePolicy;
    // 特殊管理属性
    private Integer specialManageId;
    // 给药途径
    private Integer medicationId;
    // 功能分类属性
    private Integer functionCategoryId;
    // 新特药
    private Integer specificMedId;
    // 养护标记
    private Integer careTimeId;
    // GMP属性
    private Integer gmpTypeId;
    // 经营范围属性
    private Integer scopeId;
    // 基药属性
    private Integer baseMedId;

    // 价格信息
    private Long priceConfId;
    // 库位信息
    private Long posId;

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

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
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

    public Integer getMedType() {
        return medType;
    }

    public void setMedType(Integer medType) {
        this.medType = medType;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact == null ? null : contact.trim();
    }

    public String getArchiveNumber() {
        return archiveNumber;
    }

    public void setArchiveNumber(String archiveNumber) {
        this.archiveNumber = archiveNumber == null ? null : archiveNumber.trim();
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

    public Integer getSpecialManageId() {
        return specialManageId;
    }

    public void setSpecialManageId(Integer specialManageId) {
        this.specialManageId = specialManageId;
    }

    public Integer getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Integer medicationId) {
        this.medicationId = medicationId;
    }

    public Integer getFunctionCategoryId() {
        return functionCategoryId;
    }

    public void setFunctionCategoryId(Integer functionCategoryId) {
        this.functionCategoryId = functionCategoryId;
    }

    public Integer getSpecificMedId() {
        return specificMedId;
    }

    public void setSpecificMedId(Integer specificMedId) {
        this.specificMedId = specificMedId;
    }

    public Integer getCareTimeId() {
        return careTimeId;
    }

    public void setCareTimeId(Integer careTimeId) {
        this.careTimeId = careTimeId;
    }

    public Integer getGmpTypeId() {
        return gmpTypeId;
    }

    public void setGmpTypeId(Integer gmpTypeId) {
        this.gmpTypeId = gmpTypeId;
    }

    public Integer getScopeId() {
        return scopeId;
    }

    public void setScopeId(Integer scopeId) {
        this.scopeId = scopeId;
    }

    public Integer getBaseMedId() {
        return baseMedId;
    }

    public void setBaseMedId(Integer baseMedId) {
        this.baseMedId = baseMedId;
    }

    public Long getPriceConfId() {
        return priceConfId;
    }

    public void setPriceConfId(Long priceConfId) {
        this.priceConfId = priceConfId;
    }

    public Long getPosId() {
        return posId;
    }

    public void setPosId(Long posId) {
        this.posId = posId;
    }

    public Boolean getShebao() {
        return isShebao;
    }

    public void setShebao(Boolean shebao) {
        isShebao = shebao;
    }
}