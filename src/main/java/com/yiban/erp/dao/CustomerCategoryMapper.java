package com.yiban.erp.dao;

import com.yiban.erp.entities.CustomerCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerCategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CustomerCategory record);

    CustomerCategory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerCategory record);

    List<CustomerCategory> getAllByCompanyId(Integer companyId);

    List<CustomerCategory> getByParentId(@Param("companyId") Integer companyId,
                                         @Param("parentId") Integer parentId);
}