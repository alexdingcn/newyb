package com.yiban.erp.dao;

import com.yiban.erp.entities.CustomerCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerCategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerCategory record);

    int insertSelective(CustomerCategory record);

    CustomerCategory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerCategory record);

    int updateByPrimaryKey(CustomerCategory record);

    List<CustomerCategory> getAllByCompanyId(Integer companyId);
}