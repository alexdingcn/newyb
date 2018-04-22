package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryCheckForm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepertoryCheckFormMapper {
    int deleteByPrimaryKey(Long id);
    int insert(RepertoryCheckForm record);

    int insertSelective(RepertoryCheckForm record);

    RepertoryCheckForm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryCheckForm record);

    int updateByPrimaryKey(RepertoryCheckForm record);

    List<RepertoryCheckForm> getCheckPlanFormList(Map<String, Object> paramMap);
}