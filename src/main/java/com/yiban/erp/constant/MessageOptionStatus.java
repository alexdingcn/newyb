package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 消息处理状态类型
 */
public enum MessageOptionStatus {

    UNPROCESS,
    PROCESSED,
    DELETEABLE,
    DELETE;

    public static MessageOptionStatus getByName(String status) {
        if (StringUtils.isBlank(status)) {
            return UNPROCESS;
        }
        for (MessageOptionStatus optionStatus : MessageOptionStatus.values()) {
            if (optionStatus.name().equalsIgnoreCase(status)) {
                return optionStatus;
            }
        }
        return UNPROCESS;
    }
}
