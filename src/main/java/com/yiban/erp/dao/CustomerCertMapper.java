package com.yiban.erp.dao;

import com.yiban.erp.entities.CustomerCert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerCertMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerCert record);

    int insertSelective(CustomerCert record);

    CustomerCert selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerCert record);

    int updateByPrimaryKey(CustomerCert record);

    List<CustomerCert> getByCustomerId(Integer customerId);

    int removeByIds(@Param("ids") List<Integer> ids);
}