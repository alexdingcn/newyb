package com.yiban.erp.dao;

import com.yiban.erp.entities.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Customer record);

    int insertSelective(Customer record);

    Customer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);

    Customer getCustomerDetailById(@Param("companyId") Integer companyId,
                                   @Param("customerId") Integer customerId);
}