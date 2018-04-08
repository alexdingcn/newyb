package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryCheckDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepertoryCheckDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryCheckDetail record);

    int insertSelective(RepertoryCheckDetail record);

    RepertoryCheckDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryCheckDetail record);

    int updateByPrimaryKey(RepertoryCheckDetail record);
    List<RepertoryCheckDetail> getCheckDetailList(Map<String, Object> paramMap);

}