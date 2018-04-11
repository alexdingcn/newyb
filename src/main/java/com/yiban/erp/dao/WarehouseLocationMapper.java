package com.yiban.erp.dao;

import com.yiban.erp.entities.WarehouseLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WarehouseLocationMapper {

    WarehouseLocation selectByPrimaryKey(Integer id);

    int insert(WarehouseLocation record);

    int insertSelective(WarehouseLocation record);

    int updateSelective(WarehouseLocation record);

    List<WarehouseLocation> getListByIds(@Param("ids") List<Integer> ids);

    List<WarehouseLocation> getList(Integer warehouseId);

    WarehouseLocation getByLocation(@Param("warehouseId") Integer warehouseId,
                                    @Param("location") String location);
}