package com.yiban.erp.dao;

import com.yiban.erp.entities.GoodsSpec;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsSpecMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsSpec record);

    GoodsSpec selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsSpec record);

    List<GoodsSpec> getByCompanyId(Integer companyId);

    int insertBatch(@Param("goodsSpecs") List<GoodsSpec> subGoodsSpecs);

    boolean isGoodsSpecUsed(@Param("specIds") List<Long> specIds);

    List<GoodsSpec> getSubSpecs(Long parentId);

    int deleteByIds(@Param("specIds") List<Long> specIdList);
}