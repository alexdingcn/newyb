package com.yiban.erp.dto;

import com.yiban.erp.entities.MessageInfo;

import java.util.List;

public class MessageResp {

    private Integer unProcessCount;
    private Integer processedCount;

    private List<MessageInfo> unProcessList;
    private List<MessageInfo> processedList;

    public Integer getUnProcessCount() {
        return unProcessCount;
    }

    public void setUnProcessCount(Integer unProcessCount) {
        this.unProcessCount = unProcessCount;
    }

    public Integer getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(Integer processedCount) {
        this.processedCount = processedCount;
    }

    public List<MessageInfo> getUnProcessList() {
        return unProcessList;
    }

    public void setUnProcessList(List<MessageInfo> unProcessList) {
        this.unProcessList = unProcessList;
    }

    public List<MessageInfo> getProcessedList() {
        return processedList;
    }

    public void setProcessedList(List<MessageInfo> processedList) {
        this.processedList = processedList;
    }

}
