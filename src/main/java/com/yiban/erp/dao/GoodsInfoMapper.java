package com.yiban.erp.dao;

import com.yiban.erp.entities.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsInfo record);

    GoodsInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsInfo record);

}