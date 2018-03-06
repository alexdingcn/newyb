package com.yiban.erp.dao;

import com.yiban.erp.entities.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Goods> selectAll(@Param(value = "categoryId") Integer catId,
                          @Param(value = "offset") Integer offset,
                          @Param(value = "limit") Integer limit);

    Long selectCount(@Param(value = "categoryId") Integer catId);
}