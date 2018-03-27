package com.yiban.erp.dto;

import com.yiban.erp.entities.SellOrderDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SellReviewAction {

    private String reviewType;
    private List<SellOrderDetail> detailList;
    private String checkStatus;
    private String checkResult;

    private Long sellOrderId; //根据detailList取第一个
    private List<Long> detailIdList; //根据detailList获取ID

    private Date checkDate;
    private String checkUser;

    public Long getSellOrderId() {
        if (detailList != null && !detailList.isEmpty()) {
            return detailList.get(0).getSellOrderId();
        }else {
            return null;
        }
    }

    public List<Long> getDetailIdList() {
        if (detailList != null && !detailList.isEmpty()) {
            List<Long> idList = new ArrayList<>();
            detailList.stream().forEach(item -> idList.add(item.getId()));
            return idList;
        }
        return Collections.emptyList();
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public List<SellOrderDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<SellOrderDetail> detailList) {
        this.detailList = detailList;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }
}
