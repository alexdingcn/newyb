package com.yiban.erp.dao;

import com.yiban.erp.dto.SellBackQuery;
import com.yiban.erp.entities.SellOrderBack;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SellOrderBackMapper {

    int insert(SellOrderBack record);

    SellOrderBack selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderBack record);

    List<SellOrderBack> getOrderList(SellBackQuery query);

    SellOrderBack getViewBackOrderById(Long backId);
}