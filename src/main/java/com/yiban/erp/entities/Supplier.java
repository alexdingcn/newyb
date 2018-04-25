package com.yiban.erp.entities;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Supplier {
    private Long id;

    private Integer companyId;

    private String name;

    private String pinyin;

    private Boolean enabled;

    private Integer term;

    private String city;

    private List<PlaceCode> placeCodes;

    private String address;

    private String postcode;

    private String fax;

    private String phone;

    private String email;

    private String contact;

    private String contactPhone;

    private String employee;

    private String legalPerson;

    private BigDecimal discount;

    private String bankAccount;

    private String bankName;

    private String bankNumber;

    private String taxNumber;

    private Long billingMethodId;

    private Long supplierTypeId;

    private Boolean haveStamp;

    private Boolean haveBillTemplate;

    private Boolean checkFirst;

    private Boolean isFactory;

    private Boolean isDirectSupplier;

    private Boolean isColdBusiness;

    private Boolean canSpecial;

    private String warehouseAddress;

    private String businessScope;

    private List<Long> businessScopeIdList;

    private String comment;

    private String license;

    private Date licenseExp;

    private String organizationNo;

    private Date organizationExp;

    private String gspGmpNo;

    private Date gspGmpExp;

    private String qualityProtocolNo;

    private Date qualityProtocolExp;

    private String saleProtocolNo;

    private Date saleProtocolExp;

    private String legalProtocolNo;

    private Date legalProtocolExp;

    private String otherProtocolNo;

    private Date otherProtocolExp;

    private String fileNo;

    private String createdBy;

    private String updatedBy;

    private Date createdTime;

    private Date updatedTime;

    private BigDecimal accountAmount;

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

    public List<Long> getBusinessScopeIdList() {
        if (StringUtils.isNotBlank(this.getBusinessScope())) {
            return JSON.parseArray(this.getBusinessScope(), Long.class);
        }
        return Collections.emptyList();
    }

    public void setBusinessScopeIdList(List<Long> businessScopeIdList) {
        this.businessScopeIdList = businessScopeIdList;
        if (businessScopeIdList != null && !businessScopeIdList.isEmpty()) {
            this.setBusinessScope(JSON.toJSONString(businessScopeIdList));
        }
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public List<PlaceCode> getPlaceCodes() {
        return placeCodes;
    }

    public void setPlaceCodes(List<PlaceCode> placeCodes) {
        this.placeCodes = placeCodes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public Long getBillingMethodId() {
        return billingMethodId;
    }

    public void setBillingMethodId(Long billingMethodId) {
        this.billingMethodId = billingMethodId;
    }

    public Long getSupplierTypeId() {
        return supplierTypeId;
    }

    public void setSupplierTypeId(Long supplierTypeId) {
        this.supplierTypeId = supplierTypeId;
    }

    public Boolean getHaveStamp() {
        return haveStamp;
    }

    public void setHaveStamp(Boolean haveStamp) {
        this.haveStamp = haveStamp;
    }

    public Boolean getHaveBillTemplate() {
        return haveBillTemplate;
    }

    public void setHaveBillTemplate(Boolean haveBillTemplate) {
        this.haveBillTemplate = haveBillTemplate;
    }

    public Boolean getCheckFirst() {
        return checkFirst;
    }

    public void setCheckFirst(Boolean checkFirst) {
        this.checkFirst = checkFirst;
    }

    public Boolean getFactory() {
        return isFactory;
    }

    public void setFactory(Boolean factory) {
        isFactory = factory;
    }

    public Boolean getDirectSupplier() {
        return isDirectSupplier;
    }

    public void setDirectSupplier(Boolean directSupplier) {
        isDirectSupplier = directSupplier;
    }

    public Boolean getColdBusiness() {
        return isColdBusiness;
    }

    public void setColdBusiness(Boolean coldBusiness) {
        isColdBusiness = coldBusiness;
    }

    public Boolean getCanSpecial() {
        return canSpecial;
    }

    public void setCanSpecial(Boolean canSpecial) {
        this.canSpecial = canSpecial;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Date getLicenseExp() {
        return licenseExp;
    }

    public void setLicenseExp(Date licenseExp) {
        this.licenseExp = licenseExp;
    }

    public String getOrganizationNo() {
        return organizationNo;
    }

    public void setOrganizationNo(String organizationNo) {
        this.organizationNo = organizationNo;
    }

    public Date getOrganizationExp() {
        return organizationExp;
    }

    public void setOrganizationExp(Date organizationExp) {
        this.organizationExp = organizationExp;
    }

    public String getGspGmpNo() {
        return gspGmpNo;
    }

    public void setGspGmpNo(String gspGmpNo) {
        this.gspGmpNo = gspGmpNo;
    }

    public Date getGspGmpExp() {
        return gspGmpExp;
    }

    public void setGspGmpExp(Date gspGmpExp) {
        this.gspGmpExp = gspGmpExp;
    }

    public String getQualityProtocolNo() {
        return qualityProtocolNo;
    }

    public void setQualityProtocolNo(String qualityProtocolNo) {
        this.qualityProtocolNo = qualityProtocolNo;
    }

    public Date getQualityProtocolExp() {
        return qualityProtocolExp;
    }

    public void setQualityProtocolExp(Date qualityProtocolExp) {
        this.qualityProtocolExp = qualityProtocolExp;
    }

    public String getSaleProtocolNo() {
        return saleProtocolNo;
    }

    public void setSaleProtocolNo(String saleProtocolNo) {
        this.saleProtocolNo = saleProtocolNo;
    }

    public Date getSaleProtocolExp() {
        return saleProtocolExp;
    }

    public void setSaleProtocolExp(Date saleProtocolExp) {
        this.saleProtocolExp = saleProtocolExp;
    }

    public String getLegalProtocolNo() {
        return legalProtocolNo;
    }

    public void setLegalProtocolNo(String legalProtocolNo) {
        this.legalProtocolNo = legalProtocolNo;
    }

    public Date getLegalProtocolExp() {
        return legalProtocolExp;
    }

    public void setLegalProtocolExp(Date legalProtocolExp) {
        this.legalProtocolExp = legalProtocolExp;
    }

    public String getOtherProtocolNo() {
        return otherProtocolNo;
    }

    public void setOtherProtocolNo(String otherProtocolNo) {
        this.otherProtocolNo = otherProtocolNo;
    }

    public Date getOtherProtocolExp() {
        return otherProtocolExp;
    }

    public void setOtherProtocolExp(Date otherProtocolExp) {
        this.otherProtocolExp = otherProtocolExp;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
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

    public BigDecimal getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(BigDecimal accountAmount) {
        this.accountAmount = accountAmount;
    }
}