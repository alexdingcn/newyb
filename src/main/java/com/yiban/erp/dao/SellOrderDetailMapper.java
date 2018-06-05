package com.yiban.erp.dao;

import com.yiban.erp.dto.SellReviewAction;
import com.yiban.erp.entities.SellOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SellOrderDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrderDetail record);

    SellOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderDetail record);

    List<SellOrderDetail> getDetailList(Long sellOrderId);

    List<SellOrderDetail> getDetailHistory(@Param("companyId") Integer companyId,
                                           @Param("customerId") Long customerId,
                                           @Param("goodsId") Long goodsId,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);

    int updateCheckResult(SellReviewAction action);

    List<Long> getUnCheckDetailIdList(Long sellOrderId);

    List<Long> getCheckOkDetailIdList(Long sellOrderId);

    int replaceBatch(@Param("detailList") List<SellOrderDetail> details);
}