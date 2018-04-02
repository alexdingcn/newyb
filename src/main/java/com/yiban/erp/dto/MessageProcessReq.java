package com.yiban.erp.dto;

import java.util.Date;

public class MessageProcessReq {

    private Long messageId;
    private String status;
    private String optionResult;
    private Date startDate;
    private Date endDate;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOptionResult() {
        return optionResult;
    }

    public void setOptionResult(String optionResult) {
        this.optionResult = optionResult;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
