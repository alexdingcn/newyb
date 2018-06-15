package com.yiban.erp.entities;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Customer {
    private Long id;

    private Integer companyId;

    private String customerNo;

    private Integer categoryId;

    private String categoryName;

    private String name;

    private String pinyin;

    private String status;

    private Boolean enabled;

    private Boolean canSaleSpecial;

    private Boolean isLimitSpecial;

    private String city;

    private List<PlaceCode> placeCodes;

    private String address;

    private String postcode;

    private String legalPerson;

    private String employee;

    private String contactPhone;

    private String contactFax;

    private String email;

    private Boolean isDirection;

    private String saleArea;

    private String saleMan;

    private String memberLevel;

    private String classAttOne;

    private String classAttTwo;

    private String stampTemplate;

    private String billTemplate;

    private Boolean isColdBusiness;

    private Boolean isTwoTicket;

    private String businessScope;

    private List<Long> businessScopeIdList;

    private Integer accountTerm;

    private String accountName;

    private String bankAccount;

    private String bankName;

    private String taxAccount;

    private String quaCheck;

    private String fileNo;

    private String superviseNo;

    private String comment;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private BigDecimal accountAmount;

    private Boolean  isInvoice;

    private BigDecimal  taxRate;

    private Integer  billType;

    private String  invoiceTitle;

    private CustomerCategory customerCategory; //客户分类信息

    private List<CustomerRep> customerReps; //客户代表人信息列表

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

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getCanSaleSpecial() {
        return canSaleSpecial;
    }

    public void setCanSaleSpecial(Boolean canSaleSpecial) {
        this.canSaleSpecial = canSaleSpecial;
    }

    public Boolean getLimitSpecial() {
        return isLimitSpecial;
    }

    public void setLimitSpecial(Boolean limitSpecial) {
        isLimitSpecial = limitSpecial;
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

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactFax() {
        return contactFax;
    }

    public void setContactFax(String contactFax) {
        this.contactFax = contactFax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getDirection() {
        return isDirection;
    }

    public void setDirection(Boolean direction) {
        isDirection = direction;
    }

    public String getSaleArea() {
        return saleArea;
    }

    public void setSaleArea(String saleArea) {
        this.saleArea = saleArea;
    }

    public String getSaleMan() {
        return saleMan;
    }

    public void setSaleMan(String saleMan) {
        this.saleMan = saleMan;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getClassAttOne() {
        return classAttOne;
    }

    public void setClassAttOne(String classAttOne) {
        this.classAttOne = classAttOne;
    }

    public String getClassAttTwo() {
        return classAttTwo;
    }

    public void setClassAttTwo(String classAttTwo) {
        this.classAttTwo = classAttTwo;
    }

    public String getStampTemplate() {
        return stampTemplate;
    }

    public void setStampTemplate(String stampTemplate) {
        this.stampTemplate = stampTemplate;
    }

    public String getBillTemplate() {
        return billTemplate;
    }

    public void setBillTemplate(String billTemplate) {
        this.billTemplate = billTemplate;
    }

    public Boolean getColdBusiness() {
        return isColdBusiness;
    }

    public void setColdBusiness(Boolean coldBusiness) {
        isColdBusiness = coldBusiness;
    }

    public Boolean getTwoTicket() {
        return isTwoTicket;
    }

    public void setTwoTicket(Boolean twoTicket) {
        isTwoTicket = twoTicket;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public Integer getAccountTerm() {
        return accountTerm;
    }

    public void setAccountTerm(Integer accountTerm) {
        this.accountTerm = accountTerm;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

    public String getTaxAccount() {
        return taxAccount;
    }

    public void setTaxAccount(String taxAccount) {
        this.taxAccount = taxAccount;
    }

    public String getQuaCheck() {
        return quaCheck;
    }

    public void setQuaCheck(String quaCheck) {
        this.quaCheck = quaCheck;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getSuperviseNo() {
        return superviseNo;
    }

    public void setSuperviseNo(String superviseNo) {
        this.superviseNo = superviseNo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public CustomerCategory getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(CustomerCategory customerCategory) {
        this.customerCategory = customerCategory;
    }

    public List<CustomerRep> getCustomerReps() {
        return customerReps;
    }

    public void setCustomerReps(List<CustomerRep> customerReps) {
        this.customerReps = customerReps;
    }

    public BigDecimal getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(BigDecimal accountAmount) {
        this.accountAmount = accountAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getInvoice() {
        return isInvoice;
    }

    public void setInvoice(Boolean invoice) {
        isInvoice = invoice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }
}