package com.yiban.erp.dao;

import com.yiban.erp.dto.BuyBackReq;
import com.yiban.erp.entities.RepertoryInBack;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepertoryInBackMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryInBack record);

    RepertoryInBack selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryInBack record);

    RepertoryInBack getById(Long id);

    List<RepertoryInBack> getList(BuyBackReq buyBackReq);
}