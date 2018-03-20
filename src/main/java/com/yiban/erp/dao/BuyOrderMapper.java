package com.yiban.erp.dao;

import com.yiban.erp.entities.BuyOrder;
import com.yiban.erp.entities.BuyOrderQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BuyOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BuyOrder record);

    int insertSelective(BuyOrder record);

    BuyOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BuyOrder record);

    int updateByPrimaryKey(BuyOrder record);

    List<BuyOrder> queryOrders(BuyOrderQuery buyOrder);
}