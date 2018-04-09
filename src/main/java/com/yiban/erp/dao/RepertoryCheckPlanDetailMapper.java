package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryCheckPlanDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepertoryCheckPlanDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryCheckPlanDetail record);

    int insertSelective(RepertoryCheckPlanDetail record);

    RepertoryCheckPlanDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryCheckPlanDetail record);

    int updateByPrimaryKey(RepertoryCheckPlanDetail record);

    List<RepertoryCheckPlanDetail> getCheckPlanDetailList( Map<String, Object> requestMap);
}