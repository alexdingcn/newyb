package com.yiban.erp.dao;

import com.yiban.erp.entities.GoodsBlackList;
import com.yiban.erp.entities.GoodsBlackListWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 商品详情ID获取任务规则
     * @param goodsDetailIdList
     * @return
     */
    List<GoodsBlackListWithBLOBs> getByGoodsDetailIds(@Param("goodsIds") List<Long> goodsDetailIdList);
}