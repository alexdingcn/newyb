package com.yiban.erp.entities;

import java.util.Date;

public class RepertoryCheckPart {
    private Long id;

    private String checkNo;

    private Long rCheckId;

    private Long checkUserId;

    private String partNo;
    private Long processUserId;
    private Long processResult;
    private Date processTime;
    private String createdBy;

    private Date createdTime;

    private String updatedBy;

    private Date updatedTime;

    private String partNote;


    private String processUserName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo == null ? null : checkNo.trim();
    }

    public Long getrCheckId() {
        return rCheckId;
    }

    public void setrCheckId(Long rCheckId) {
        this.rCheckId = rCheckId;
    }

    public Long getCheckUserId() {
        return checkUserId;
    }

    public void setCheckUserId(Long checkUserId) {
        this.checkUserId = checkUserId;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo == null ? null : partNo.trim();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy == null ? null : updatedBy.trim();
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getPartNote() {
        return partNote;
    }

    public void setPartNote(String partNote) {
        this.partNote = partNote == null ? null : partNote.trim();
    }

    public Long getProcessUserId() {
        return processUserId;
    }

    public void setProcessUserId(Long processUserId) {
        this.processUserId = processUserId;
    }

    public Long getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Long processResult) {
        this.processResult = processResult;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public String getProcessUserName() {
        return processUserName;
    }

    public void setProcessUserName(String processUserName) {
        this.processUserName = processUserName;
    }
}