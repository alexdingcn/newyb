package com.yiban.erp.dao;

import com.yiban.erp.entities.CustomerRep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface CustomerRepMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerRep record);

    CustomerRep selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerRep record);

    CustomerRep getDefault(Integer customerId);

    int setDefault(@Param("id") Integer id,
                   @Param("defaultTime")Date defaultTime);

    List<CustomerRep> getByCustomerId(Integer customerId);

}