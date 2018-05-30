package com.yiban.erp.dao;

import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.RepertorySelectQuery;
import com.yiban.erp.entities.RepertoryInfo;
import com.yiban.erp.dto.RepertoryQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface RepertoryInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryInfo record);

    int insertBatch(@Param("list") List<RepertoryInfo> infoList);

    RepertoryInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryInfo record);

    List<RepertoryInfo> getListByIdList(@Param("idList") List<Long> repertoryIdList);

    List<String> getGoodNameWithLessQuantity(Long sellOrderId);

    Integer  queryRepertoryCount(RepertoryQuery repertoryQuery);

    List<RepertoryInfo> queryRepertoryPage(RepertoryQuery repertoryQuery);

    //减库存
    int sellOrderConsumeQuantity(@Param("sellOrderId") Long sellOrderId,
                                 @Param("updateBy") String updateBy,
                                 @Param("updateTime") Date updateTime);

    //获取当前仓库中某一类商品的存量
    List<CurrentBalanceResp> getBalance(@Param("warehouseId") Integer warehouseId, @Param("goodsIdList") List<Long> goodsIdList);
    //获取某一商品最近一次的采购价
    List<CurrentBalanceResp> getLastBuyPrice(@Param("warehouseId") Integer warehouseId, @Param("goodsIdList") List<Long> goodsIdList);

    Integer querySelectCount(@Param("query") RepertorySelectQuery query,
                             @Param("goodsIds") List<Long> goods); //选择存库商品列表的查询项

    List<RepertoryInfo> querySelectList(@Param("query") RepertorySelectQuery query,
                                        @Param("goodsIds") List<Long> goods);

    int updateOnWayQuantity(@Param("id") Long id, @Param("onWayQuantity") BigDecimal backQuantity);

    int buyBackConsumeQuantity(@Param("inBackId") Long inBackId,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    int buyBackReleaseOnWayQuantity(@Param("inBackId") Long inBackId); //释放锁定的在单数量

    int sellOrderReleaseOnWayQuantity(@Param("sellOrderId") Long sellOrderId); //释放锁定的销售在单数量

    Long getGoodsId(@Param("attrId") Long attId,
                    @Param("attrValue") String attrValue);
}