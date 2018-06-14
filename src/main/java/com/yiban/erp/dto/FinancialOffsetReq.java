package com.yiban.erp.dto;

public class FinancialOffsetReq {

    private String bizType;  //只有两种，预收款或者预付款
    private Long preRecordId; //收款或者付款记录的ID
//    private String refBizNo; //冲销关联的往来账流水号
    private String keyWord; //冲销流水摘要信息

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Long getPreRecordId() {
        return preRecordId;
    }

    public void setPreRecordId(Long preRecordId) {
        this.preRecordId = preRecordId;
    }

//    public String getRefBizNo() {
//        return refBizNo;
//    }
//
//    public void setRefBizNo(String refBizNo) {
//        this.refBizNo = refBizNo;
//    }
}
