package com.yiban.erp.dao;

import com.yiban.erp.entities.BillTradeLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillTradeLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BillTradeLog record);

}