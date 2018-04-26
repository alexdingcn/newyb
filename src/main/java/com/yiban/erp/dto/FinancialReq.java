package com.yiban.erp.dto;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialReq {

    public static final String CUST_SUPPLIER = "SUPPLIER"; //供应商类型
    public static final String CUST_CUSTOMER = "CUSTOMER"; //客户类型

    public Integer companyId;
    public String logUserName; //记账人


    private String custType; //针对的客户类型，只有两种，供应商或者客户
    private Long custId; //客户类型对应的供应商ID或者客户ID

    private String bizType; //业务类型,对应具体的操作类型
    private Long bizRefId; //关联的业务单的ID
    private String bizRefNo; //关联业务单的流水号
    private BigDecimal logAmount; //发生金额

    private Date logDate; //记账日期
    private String logAccount; //记账账户(逻辑上就是供应上或者客户的名称)
    private String docNo; //记账凭证号
    private String fileNo; //上传的档案编号
    private String keyWord; //摘要信息

    private Long payMethod; //支付方式
    private String companyAccount; //公司账号(清算账号)
    private String custAccount; //客户账号

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getLogUserName() {
        return logUserName;
    }

    public void setLogUserName(String logUserName) {
        this.logUserName = logUserName;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getBizRefId() {
        return bizRefId;
    }

    public void setBizRefId(Long bizRefId) {
        this.bizRefId = bizRefId;
    }

    public String getBizRefNo() {
        return bizRefNo;
    }

    public void setBizRefNo(String bizRefNo) {
        this.bizRefNo = bizRefNo;
    }

    public BigDecimal getLogAmount() {
        return logAmount;
    }

    public void setLogAmount(BigDecimal logAmount) {
        this.logAmount = logAmount;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getLogAccount() {
        return logAccount;
    }

    public void setLogAccount(String logAccount) {
        this.logAccount = logAccount;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Long getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Long payMethod) {
        this.payMethod = payMethod;
    }

    public String getCompanyAccount() {
        return companyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        this.companyAccount = companyAccount;
    }

    public String getCustAccount() {
        return custAccount;
    }

    public void setCustAccount(String custAccount) {
        this.custAccount = custAccount;
    }
}
