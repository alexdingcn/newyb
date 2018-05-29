package com.yiban.erp.dao;

import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.GoodsCare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsCareMapper {

    Long searchListCount(GoodsQuery query);
    List<GoodsCare> searchList (GoodsQuery query);
    Integer save(GoodsCare goodsCare);
    List<GoodsCare> searchRecord(GoodsCare goodsCare);
    Integer searchRecordCount(GoodsCare goodsCare);
    GoodsCare queryGoods(@Param("goodsId") Long id);
}
