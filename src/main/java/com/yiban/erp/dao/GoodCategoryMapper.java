package com.yiban.erp.dao;

import com.yiban.erp.entities.GoodCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodCategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GoodCategory record);

    GoodCategory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GoodCategory record);

    List<GoodCategory> selectAll(Integer companyId);

    int goodsInfoUseCategory(Integer id);

    Integer subCategoryCount(Integer id);
}