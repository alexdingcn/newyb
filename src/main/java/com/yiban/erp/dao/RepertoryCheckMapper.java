package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryCheck;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepertoryCheckMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryCheck record);

    int insertSelective(RepertoryCheck record);

    RepertoryCheck selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryCheck record);

    int updateByPrimaryKey(RepertoryCheck record);

    List<RepertoryCheck> getCheckList(Map<String, Object> paramMap);
}