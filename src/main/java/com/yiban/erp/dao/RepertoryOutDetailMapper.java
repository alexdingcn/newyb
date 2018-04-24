package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryOutDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepertoryOutDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryOutDetail record);

    RepertoryOutDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryOutDetail record);

    int insertBatch(@Param("details") List<RepertoryOutDetail> outDetails);
}