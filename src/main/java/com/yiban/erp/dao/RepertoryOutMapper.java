package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryOut;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepertoryOutMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryOut record);

    RepertoryOut selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryOut record);

}