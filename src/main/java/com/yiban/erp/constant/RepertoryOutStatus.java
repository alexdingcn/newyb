package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

public enum RepertoryOutStatus {

    TEMP_STORAGE,
    INIT,
    REVIEW,
    REVIEW_NEXT,
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
