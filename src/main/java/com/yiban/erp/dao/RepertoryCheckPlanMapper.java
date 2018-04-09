package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryCheckPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepertoryCheckPlanMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryCheckPlan record);

    int insertSelective(RepertoryCheckPlan record);

    RepertoryCheckPlan selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryCheckPlan record);

    int updateByPrimaryKey(RepertoryCheckPlan record);

    List<RepertoryCheckPlan> getCheckPlanList( Map<String, Object> requestMap);

}