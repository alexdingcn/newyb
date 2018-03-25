package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepertoryInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RepertoryInfo record);

    int insertSelective(RepertoryInfo record);

    RepertoryInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RepertoryInfo record);

    int updateByPrimaryKey(RepertoryInfo record);

    Integer getDetailListCount(Map<String, Object> paramMap);

    List<RepertoryInfo> getDetailList(Map<String, Object> paramMap);

    List<RepertoryInfo> getListByIdList(@Param("idList") List<Long> repertoryIdList);
}