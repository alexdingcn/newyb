package com.yiban.erp.dao;

import com.yiban.erp.entities.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface WarehouseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Warehouse record);

    int insertSelective(Warehouse record);

    Warehouse selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Warehouse record);

    int updateByPrimaryKey(Warehouse record);

    List<Warehouse> getWarehouses(@Param("companyId") Integer companyId);

    int updateStatus(@Param("id") Integer id,
                     @Param("toStatus") String status,
                     @Param("updatedBy") String updateBy,
                     @Param("updatedTime")Date updateTime);

}