package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryInventoryDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepertoryInventoryDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryInventoryDetail record);

    RepertoryInventoryDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryInventoryDetail record);

    List<RepertoryInventoryDetail> getDetails(Long inventoryId);

    int insertBatch(@Param("details") List<RepertoryInventoryDetail> details);

    int deleteByInventoryId(Long id);
}