package com.yiban.erp.dto;

import org.apache.commons.lang3.StringUtils;

public class GoodsQuery {

    private Integer companyId;
    private Integer categoryId;
    private Long brandId;
    private Long supplierId;
    private Boolean enable;
    private String status;
    private String search; //名称、拼音、编号模糊匹配
    private Integer page;
    private Integer pageSize;

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
}
