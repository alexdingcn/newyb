package com.yiban.erp.entities;

import com.yiban.erp.constant.MessageOptionStatus;

import java.util.Date;
import java.util.List;

public class MessageInfo {
    private Long id;

    private Integer companyId;

    private String status;

    private String roleType;

    private String title;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String content;

    private List<MessageOption> options;


    /**
     * 判断一个消息针对某一个人是处于何种状态
     * @param userId
     * @return
     */
    public MessageOptionStatus userMessageStatus(Long userId) {
        if (options == null || options.isEmpty()) {
            return MessageOptionStatus.UNPROCESS;
        }
        for (MessageOption option : options) {
            if (!option.getUserId().equals(userId)) {
                continue;
            }
            if (MessageOptionStatus.PROCESSED.name().equalsIgnoreCase(option.getOptionStatus())){
                return MessageOptionStatus.PROCESSED;
            }
        }
        return MessageOptionStatus.UNPROCESS;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType == null ? null : roleType.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public List<MessageOption> getOptions() {
        return options;
    }

    public void setOptions(List<MessageOption> options) {
        this.options = options;
    }
}