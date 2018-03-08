package com.yiban.erp.dao;

import com.yiban.erp.entities.CustomerCert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerCertMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerCert record);

    int insertSelective(CustomerCert record);

    CustomerCert selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerCert record);

    int updateByPrimaryKey(CustomerCert record);
}