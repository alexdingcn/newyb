package com.yiban.erp.dao;

import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.entities.BuyOrder;
import com.yiban.erp.entities.BuyOrderQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BuyOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BuyOrder record);

    BuyOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BuyOrder record);

    List<BuyOrder> queryOrders(BuyOrderQuery buyOrder);

    BuyOrder getOrderById(Long orderId);

    List<CurrentBalanceResp> getGoodsOrderCount(@Param("goodsIdList") List<Long> goodsIdList);
}