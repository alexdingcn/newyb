package com.yiban.erp.dao;

import com.yiban.erp.entities.WarehouseLocation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WarehouseLocationMapper {
    int insert(WarehouseLocation record);

    int insertSelective(WarehouseLocation record);

    int updateSelective(WarehouseLocation record);
}