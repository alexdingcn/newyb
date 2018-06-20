package com.yiban.erp.dao;

import com.yiban.erp.dto.InventorySearch;
import com.yiban.erp.entities.RepertoryInventory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepertoryInventoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryInventory record);

    RepertoryInventory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryInventory record);

    List<RepertoryInventory> getOrderList(InventorySearch search);

    Integer getOrderListCount(InventorySearch search);

    int warehouseHaveUncheckCount(Integer warehouseId);

    RepertoryInventory getInventory(Long inventoryId);
}