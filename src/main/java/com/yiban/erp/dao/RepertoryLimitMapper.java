package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryLimit;

public interface RepertoryLimitMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RepertoryLimit record);

    int insertSelective(RepertoryLimit record);

    RepertoryLimit selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RepertoryLimit record);

    int updateByPrimaryKey(RepertoryLimit record);
}