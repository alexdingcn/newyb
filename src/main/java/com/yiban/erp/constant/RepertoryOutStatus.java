package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

public enum RepertoryOutStatus {

    TEMP_STORAGE,
    //待复核
    INIT,
    //复核中
    REVIEW,
    //双人复核
    REVIEW_NEXT,
    //已审核
    CHECKED,
    DELETE,

    BACK_INIT,
    BACK_SELL_CHECK,
    BACK_QUALITY_CHECK,
    BACK_QUALITY_RECHECK,
    BACK_FINAL_CHECK
    ;

    public static RepertoryOutStatus getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (RepertoryOutStatus status : RepertoryOutStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
