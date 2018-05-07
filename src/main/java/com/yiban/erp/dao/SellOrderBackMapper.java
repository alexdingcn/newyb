package com.yiban.erp.dao;

import com.yiban.erp.entities.SellOrderBack;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SellOrderBackMapper {

    int insert(SellOrderBack record);

    SellOrderBack selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderBack record);

}