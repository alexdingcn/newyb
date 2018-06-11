package com.yiban.erp.dao;

import com.yiban.erp.entities.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface GoodsSourceMapper {

    List<Goods> queryGoods(@Param("companyId") Integer companyId);
    List<String> getBatch(@Param("goodId") Long goodId);
    //Long getGoodIdByDetail(@Param("detailId") Long detailId);
    Integer countOnSell(@Param("goodId")Long goodId,@Param("batchCode")String batchCode);
    Integer countStock(@Param("goodId")Long goodId,@Param("batchCode")String batchCode);
    List<SellOrderDetail> getSell(@Param("goodId")Long goodId, @Param("batchCode")String batchCode);
    List<RepertoryInDetail> getBuy(@Param("goodId")Long goodId, @Param("batchCode")String batchCode);
    RepertoryOut getRepertoryOuts(@Param("Id") Long Id);
    RepertoryOutDetail getRepertoryOutDetail(@Param("Id") Long Id);
    int destory(RepertoryOut repertoryOut);
    int destoryDetail(RepertoryOutDetail repertoryOutDetail);
    List<Long> getRepertoryId(@Param("goodId")Long goodId, @Param("batchCode")String batchCode);

}
