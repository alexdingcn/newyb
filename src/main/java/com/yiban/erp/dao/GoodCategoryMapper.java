package com.yiban.erp.dao;

import com.yiban.erp.entities.GoodCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodCategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByName(@Param(value = "name") String name);

    int insert(GoodCategory record);

    int insertSelective(GoodCategory record);

    GoodCategory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GoodCategory record);

    int updateByPrimaryKey(GoodCategory record);

    List<GoodCategory> selectAll(Integer companyId);
}