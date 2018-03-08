package com.yiban.erp.dao;

import com.yiban.erp.entities.CustomerRep;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerRepMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerRep record);

    int insertSelective(CustomerRep record);

    CustomerRep selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerRep record);

    int updateByPrimaryKey(CustomerRep record);
}