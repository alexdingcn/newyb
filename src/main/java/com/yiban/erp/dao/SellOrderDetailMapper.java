package com.yiban.erp.dao;

import com.yiban.erp.entities.SellOrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SellOrderDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrderDetail record);

    int insertSelective(SellOrderDetail record);

    SellOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderDetail record);

    int updateByPrimaryKey(SellOrderDetail record);

    List<SellOrderDetail> getDetailList(Long sellOrderId);
}