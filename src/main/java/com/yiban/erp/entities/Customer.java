package com.yiban.erp.entities;

import java.util.Date;
import java.util.List;

public class Customer {
    private Integer id;

    private Integer companyId;

    private String customerNo;

    private String name;

    private String shorName;

    private String pinyin;

    private String status;

    private Boolean isDisable; //是否禁用 true-警用，false-启用

    private Boolean canSaleSpecial;

    private Boolean isLimitSpecial;

    private String city;

    private String address;

    private String postcode;

    private Integer categoryId;

    private String legalPersion;

    private String employee;

    private String contactPhone;

    private String contactFax;

    private String email;

    private Boolean isDirection;

    private String saleArea;

    private String salesman;

    private String memberLevel;

    private String classAttOne;

    private String classAttTwo;

    private String sealModel;

    private String billModel;

    private String businessScope;

    private Integer accountPeriod;

    private String accountName;

    private String bankAccount;

    private String bankName;

    private String dutyAccount;

    private String quaCheck;

    private String fileNo;

    private String superviseNo;

    private String comment;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private CustomerCategory customerCategory; //客户分类信息

    private List<CustomerCert> customerCerts;   //客户证件列表

    private List<CustomerRep> customerReps; //客户代表人信息列表

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getShorName() {
        return shorName;
    }

    public void setShorName(String shorName) {
        this.shorName = shorName == null ? null : shorName.trim();
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDisable() {
        return isDisable;
    }

    public void setDisable(Boolean disable) {
        isDisable = disable;
    }

    public Boolean getCanSaleSpecial() {
        return canSaleSpecial;
    }

    public void setCanSaleSpecial(Boolean canSaleSpecial) {
        this.canSaleSpecial = canSaleSpecial;
    }

    public Boolean getIsLimitSpecial() {
        return isLimitSpecial;
    }

    public void setIsLimitSpecial(Boolean isLimitSpecial) {
        this.isLimitSpecial = isLimitSpecial;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode == null ? null : postcode.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getLegalPersion() {
        return legalPersion;
    }

    public void setLegalPersion(String legalPersion) {
        this.legalPersion = legalPersion == null ? null : legalPersion.trim();
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee == null ? null : employee.trim();
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    public String getContactFax() {
        return contactFax;
    }

    public void setContactFax(String contactFax) {
        this.contactFax = contactFax == null ? null : contactFax.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Boolean getIsDirection() {
        return isDirection;
    }

    public void setIsDirection(Boolean isDirection) {
        this.isDirection = isDirection;
    }

    public String getSaleArea() {
        return saleArea;
    }

    public void setSaleArea(String saleArea) {
        this.saleArea = saleArea == null ? null : saleArea.trim();
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman == null ? null : salesman.trim();
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel == null ? null : memberLevel.trim();
    }

    public String getClassAttOne() {
        return classAttOne;
    }

    public void setClassAttOne(String classAttOne) {
        this.classAttOne = classAttOne == null ? null : classAttOne.trim();
    }

    public String getClassAttTwo() {
        return classAttTwo;
    }

    public void setClassAttTwo(String classAttTwo) {
        this.classAttTwo = classAttTwo == null ? null : classAttTwo.trim();
    }

    public String getSealModel() {
        return sealModel;
    }

    public void setSealModel(String sealModel) {
        this.sealModel = sealModel;
    }

    public String getBillModel() {
        return billModel;
    }

    public void setBillModel(String billModel) {
        this.billModel = billModel;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope == null ? null : businessScope.trim();
    }

    public Integer getAccountPeriod() {
        return accountPeriod;
    }

    public void setAccountPeriod(Integer accountPeriod) {
        this.accountPeriod = accountPeriod;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getDutyAccount() {
        return dutyAccount;
    }

    public void setDutyAccount(String dutyAccount) {
        this.dutyAccount = dutyAccount == null ? null : dutyAccount.trim();
    }

    public String getSuperviseNo() {
        return superviseNo;
    }

    public void setSuperviseNo(String superviseNo) {
        this.superviseNo = superviseNo == null ? null : superviseNo.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
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
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public CustomerCategory getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(CustomerCategory customerCategory) {
        this.customerCategory = customerCategory;
    }

    public List<CustomerCert> getCustomerCerts() {
        return customerCerts;
    }

    public void setCustomerCerts(List<CustomerCert> customerCerts) {
        this.customerCerts = customerCerts;
    }

    public List<CustomerRep> getCustomerReps() {
        return customerReps;
    }

    public void setCustomerReps(List<CustomerRep> customerReps) {
        this.customerReps = customerReps;
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
}