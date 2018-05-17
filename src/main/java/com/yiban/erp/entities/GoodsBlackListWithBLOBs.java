package com.yiban.erp.entities;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class GoodsBlackListWithBLOBs extends GoodsBlackList {
    private String regions;

    private String customerIds;

    private String customerCategoryIds;

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions == null ? null : regions.trim();
    }

    public String getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(String customerIds) {
        this.customerIds = customerIds == null ? null : customerIds.trim();
    }

    public String getCustomerCategoryIds() {
        return customerCategoryIds;
    }

    public void setCustomerCategoryIds(String customerCategoryIds) {
        this.customerCategoryIds = customerCategoryIds == null ? null : customerCategoryIds.trim();
    }

    public JSONObject getJsonFormat() {
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(this.getRegions())) {
            obj.put("regions", JSON.parseArray(this.getRegions()));
        }
        if (StringUtils.isNotBlank(this.getCustomerIds())) {
            obj.put("customerIds", JSON.parseArray(this.getCustomerIds()));
        }
        if (StringUtils.isNotBlank(this.getCustomerCategoryIds())) {
            obj.put("customerCategoryIds", JSON.parseArray(this.getCustomerCategoryIds()));
        }
        return obj;
    }
}