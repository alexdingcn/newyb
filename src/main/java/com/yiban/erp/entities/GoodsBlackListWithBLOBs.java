package com.yiban.erp.entities;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GoodsBlackListWithBLOBs extends GoodsBlackList {

    private String goodsName;

    private String regions;

    private String customerIds;

    private String customerCategoryIds;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

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

    public boolean isEmpty() {
        if ("[]".equals(this.getCustomerCategoryIds()) &&
                "[]".equalsIgnoreCase(this.getCustomerIds()) &&
                "[]".equalsIgnoreCase(this.getRegions())) {
            return true;
        }
        return false;
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

    public Map<Long, Customer> getCustomerMap() {
        Map<Long, Customer> result = new HashMap<>();
        if (StringUtils.isNotBlank(this.getCustomerIds())) {
            List<Customer> customers = JSON.parseArray(this.customerIds, Customer.class);
            customers.stream().forEach(item -> result.put(item.getId(), item));
        }
        return result;
    }

    public Set<Integer> getCustomerCategoryIdSet() {
        Set<Integer> result = new HashSet<>();
        if (StringUtils.isNotBlank(this.customerCategoryIds)) {
            List<Integer> custCategorys = JSON.parseArray(this.customerCategoryIds, Integer.class);
            custCategorys.stream().forEach(item -> result.add(item));
        }
        return result;
    }

    /**
     * 输入客户的城市列表
     * 返回是否存在限制列表
     * @param placeCodes
     * @return
     */
    public boolean isInRegionsBlack(List<PlaceCode> placeCodes) {
        if (placeCodes == null || placeCodes.isEmpty()) {
            return true; //没有所在城市数据时直接认为通过
        }
        if (StringUtils.isBlank(this.regions)) {
            return true; //没有配置地域限制
        }

        //如果存在，把code拼接重字符串，使用字符串比较方式
        StringBuilder codeStr = new StringBuilder();
        for (PlaceCode code : placeCodes) {
            codeStr.append(code.getCode());
        }
        String code = codeStr.toString().trim();

        //把所有的地域限制全部验证一次，遇到匹配的直接返回
        List<PlaceCode> regions = JSON.parseArray(this.regions, PlaceCode.class);
        for (PlaceCode item : regions) {
            //注意，获取code中的内容还需要解析一次
            String value = item.getCode();
            List<PlaceCode> codes = JSON.parseArray(value, PlaceCode.class);
            StringBuilder blackCode = new StringBuilder();
            if (codes != null && !codes.isEmpty()) {
                codes.stream().forEach(placeItem -> blackCode.append(placeItem.getCode()));
            }
            if (blackCode.toString().trim().startsWith(code)) {
                //存在地域限制
                return false;
            }
        }
        return true;
    }


}