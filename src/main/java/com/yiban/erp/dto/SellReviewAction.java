package com.yiban.erp.dto;

import com.yiban.erp.entities.SellOrderDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SellReviewAction {

    private String reviewType;
    private List<SellOrderDetail> detailList;
    private String reviewStatus;
    private String reviewComment;

    private Long sellOrderId; //根据detailList取第一个
    private List<Long> detailIdList; //根据detailList获取ID

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

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}
