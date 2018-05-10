package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

public enum SellBackStatus {

    INIT,
    INIT_SALE_CHECKED,
    INIT_QUALITY_CHECKED,
    BACK_RECEIVE,
    QUALITY_CHECKED,
    FINAL_CHECKED,
    DELETE;

    public static SellBackStatus getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (SellBackStatus status : SellBackStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }


}
