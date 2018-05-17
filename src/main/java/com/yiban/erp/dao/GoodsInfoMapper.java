package com.yiban.erp.dao;

import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsInfo record);

    GoodsInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsInfo record);

    Long searchListCount(GoodsQuery query);

    List<GoodsInfo> searchList(GoodsQuery query);

    boolean isGoodsUsed(Long id); //检查一个商品下的所有详情是否存在使用的情况，如果存在，不能删除（验证的是采购进货表）

    /**
     * 展开式获取产品详情信息的记录条数
     * @param query
     * @return
     */
    Long getChooseListDetailCount(GoodsQuery query);

    /**
     * 详情全部展开的模式进行查询
     * @param query
     * @return
     */
    List<Goods> getChooseListDetail(GoodsQuery query);
    /**
     * 详情全部展开的模式进行查询
     * @param ids 列表查询
     * @return
     */
    List<Goods> getChooseListDetailById(@Param("ids") List<Long> ids);

    int updatePrice(GoodsInfo goodsInfo);
}