package com.yiban.erp.dao;

import com.yiban.erp.entities.BuyOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BuyOrderDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BuyOrderDetail record);

    int insertSelective(BuyOrderDetail record);

    BuyOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BuyOrderDetail record);

    int updateByPrimaryKey(BuyOrderDetail record);

    List<BuyOrderDetail> findByOrderId(@Param("buyOrderId") Long buyOrderId);

    int deleteByBuyOrderId(@Param("buyOrderId") Long buyOrderId);
}