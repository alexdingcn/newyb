package com.yiban.erp.dto;

public class PagedQuery {

    private Integer companyId;
    private String search; // 品牌名称/拼音/编号 模糊匹配
    private Boolean enabled;
    private Integer page;
    private Integer pageSize;

    private Integer offset;
    private Integer limit;

    public Integer getOffset() {
        Integer size = this.getLimit();
        if (size == null) {
            return null;
        }
        if (this.page == null || this.page <= 0) {
            return null;
        }
        return (this.page - 1) * size;
    }
    public Integer getLimit() {
        if (this.pageSize == null || this.pageSize <= 0) {
            return null;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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




}
