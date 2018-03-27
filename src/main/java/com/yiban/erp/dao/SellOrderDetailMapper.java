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

    int insertSelective(SellOrderDetail record);

    SellOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderDetail record);

    int updateByPrimaryKey(SellOrderDetail record);

    List<SellOrderDetail> getDetailList(Long sellOrderId);

    List<SellOrderDetail> getDetailHistory(@Param("companyId") Integer companyId,
                                           @Param("customerId") Integer customerId,
                                           @Param("goodIds") List<Long> goodIds,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);

    int updateCheckResult(@Param("detailIdList") List<Long> detailIdList,
                          @Param("reviewAction")SellReviewAction action,
                          @Param("updateTime")Date updateTime,
                          @Param("updateBy") String updateBy);

    List<Long> getUnCheckDetailIdList(Long sellOrderId);

    List<Long> getCheckOkDetailIdList(Long sellOrderId);

    int replaceBatch(@Param("detailList") List<SellOrderDetail> details);
}