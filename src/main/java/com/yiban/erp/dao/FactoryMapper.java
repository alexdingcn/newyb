package com.yiban.erp.dao;

import com.yiban.erp.entities.Factory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FactoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Factory record);

    int insertSelective(Factory record);

    Factory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Factory record);

    int updateByPrimaryKey(Factory record);

    List<Factory> selectAll(Integer companyId);

    List<Factory> searchByNameOrContact(
            @Param(value = "companyId") Integer companyId,
            @Param(value = "searchStr") String searchStr);
}