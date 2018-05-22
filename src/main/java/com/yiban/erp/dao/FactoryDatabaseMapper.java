package com.yiban.erp.dao;

import com.yiban.erp.entities.FactoryDatabase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FactoryDatabaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FactoryDatabase record);

    int insertSelective(FactoryDatabase record);

    FactoryDatabase selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FactoryDatabase record);

    int updateByPrimaryKey(FactoryDatabase record);

    List<FactoryDatabase> searchByNameOrPermit(
            @Param(value = "companyName") String companyName,
            @Param(value = "license") String license,
            @Param(value = "permit") String permit);
}