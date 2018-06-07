package com.yiban.erp.dto;

import com.yiban.erp.entities.GoodsAttributeRef;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GoodsQuery {

    public static final String OPTION_LW = "LW"; //当前库存
    public static final String OPTION_LB = "LB"; //最近一次采购价
    public static final String OPTION_CBQ = "CBQ"; //当前采购在单数量
    public static final String OPTION_LS = "LS";

    private Integer companyId;
    private Integer categoryId;
    private Long brandId;
    private Long supplierId;
    private Long factoryId;
    private Boolean enable;
    private String status;
    private String search; //名称、拼音、编号模糊匹配
    private Integer page;
    private Integer pageSize;

    private Boolean includeDetail;

    private List<GoodsAttributeRef>  defaultAttr;//自定义属性

    private Long defaultGoodId;//存放自定义属性的商品id 用来与商品关联

    /**
     * LW:当前库存，
     * LB: last buy 最近采购价
     * LS: last sale 最近一次销售价
     */
    private List<String> options; //辅助查询项，空值是否查询库存，最近一次销售价，最近一次采购价
    private Integer warehouseId; //查库存和采购销售价，需要配合warehouseId查询
    private Long customerId; //配合options查询最近一次某一个客户的销售价时使用

    private Integer offset;
    private Integer limit;

    public Integer getOffset() {
        return (this.page == null || this.page <= 0 ? 0 : this.page - 1) * this.getLimit();
    }

    public Integer getLimit() {
        if (this.pageSize == null || this.pageSize <=0 ) {
            return 30;
        }else {
            return this.pageSize;
        }
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getStatus() {
        if (status == null || StringUtils.isEmpty(status.trim())) {
            return null;
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        if (search == null || StringUtils.isEmpty(search.trim())) {
            return null;
        }
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getIncludeDetail() {
        return includeDetail;
    }

    public void setIncludeDetail(Boolean includeDetail) {
        this.includeDetail = includeDetail;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<GoodsAttributeRef> getDefaultAttr() { return defaultAttr; }

    public void setDefaultAttr(List<GoodsAttributeRef> defaultAttr) { this.defaultAttr = defaultAttr; }

    public Long getDefaultGoodId() {
        return defaultGoodId;
    }

    public void setDefaultGoodId(Long defaultGoodId) {
        if(defaultAttr.size()>0){
            defaultGoodId=defaultAttr.get(0).getGoodsInfoId();
        }
        this.defaultGoodId = defaultGoodId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }
}
