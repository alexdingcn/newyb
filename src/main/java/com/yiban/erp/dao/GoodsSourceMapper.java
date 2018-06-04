package com.yiban.erp.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsSourceMapper {

    List<String> getBatch(@Param("goodId") Long goodId);
}
