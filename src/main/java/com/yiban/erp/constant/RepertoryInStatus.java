package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

public enum RepertoryInStatus {

    TEMP_STORAGE,
    INIT,
    CHECKED,
    IN_CHECKED,
    DELETE,

    BACK_INIT,
    BACK_BUY_CHECK,
    BACK_QUALITY_CHECK,
    BACK_QUALITY_RECHECK,
    BACK_FINAL_CHECK
    ;

    public static RepertoryInStatus getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (RepertoryInStatus status : RepertoryInStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
