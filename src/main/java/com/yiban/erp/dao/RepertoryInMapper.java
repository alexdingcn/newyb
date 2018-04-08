package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.dto.InOrderQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RepertoryInMapper {
    int insert(RepertoryIn record);

    int insertSelective(RepertoryIn record);

    List<RepertoryIn> queryInOrders(InOrderQuery inquery);
}