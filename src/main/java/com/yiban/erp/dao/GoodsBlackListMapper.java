package com.yiban.erp.dao;

import com.yiban.erp.entities.GoodsBlackList;
import com.yiban.erp.entities.GoodsBlackListWithBLOBs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsBlackListMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsBlackListWithBLOBs record);

    int insertSelective(GoodsBlackListWithBLOBs record);

    GoodsBlackListWithBLOBs selectByPrimaryKey(Long id);

    GoodsBlackListWithBLOBs selectByGoodsId(Long goodsId);

    int updateByPrimaryKeySelective(GoodsBlackListWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(GoodsBlackListWithBLOBs record);

    int updateByPrimaryKey(GoodsBlackList record);
}