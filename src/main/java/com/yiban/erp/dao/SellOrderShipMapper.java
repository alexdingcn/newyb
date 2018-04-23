package com.yiban.erp.dao;

import com.yiban.erp.entities.SellOrderShip;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SellOrderShipMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrderShip record);

    SellOrderShip selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderShip record);

    List<SellOrderShip> getBySellOrderId(Long orderId);
}