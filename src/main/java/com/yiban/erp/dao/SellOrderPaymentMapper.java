package com.yiban.erp.dao;

import com.yiban.erp.dto.SellOrderQuery;
import com.yiban.erp.entities.SellOrderPayment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SellOrderPaymentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrderPayment record);

    int insertSelective(SellOrderPayment record);

    SellOrderPayment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderPayment record);

    int updateByPrimaryKey(SellOrderPayment record);

    List<SellOrderPayment> getPaymentHistory(SellOrderQuery query);

    Integer getPaymentHistoryCount(SellOrderQuery query);
}