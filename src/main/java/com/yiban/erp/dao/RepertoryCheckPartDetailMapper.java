package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryCheckPartDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepertoryCheckPartDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryCheckPartDetail record);

    int insertSelective(RepertoryCheckPartDetail record);

    RepertoryCheckPartDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryCheckPartDetail record);

    int updateByPrimaryKey(RepertoryCheckPartDetail record);
}