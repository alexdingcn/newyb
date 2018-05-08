package com.yiban.erp.dao;

import com.yiban.erp.dto.SellOrderAllAction;
import com.yiban.erp.dto.SellReviewOrderQuery;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.StatusCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SellOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrder record);

    SellOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrder record);

    Integer getListCount(@Param("companyId") Integer companyId,
                            @Param("customerId") Long customerId,
                            @Param("saleId") Long saleId,
                            @Param("refNo") String refNo,
                            @Param("status") String status,
                         @Param("createOrderDate") Date createOrderDate);

    List<SellOrder> getList(@Param("companyId") Integer companyId,
                            @Param("customerId") Long customerId,
                            @Param("saleId") Long saleId,
                            @Param("refNo") String refNo,
                            @Param("status") String status,
                            @Param("createOrderDate") Date createOrderDate,
                            @Param("limit") int limit,
                            @Param("offset") int offset);

    List<SellOrder> getAllList(SellOrderAllAction params);

    Integer getAllCount(SellOrderAllAction params);

    List<SellOrder> getListById(@Param("idList") List<Long> idList);

    SellOrder getReviewDetailById(Long id);

    List<SellOrder> getReviewOrderList(SellReviewOrderQuery query);

    List<StatusCount> getOrderStatusStat(@Param("companyId") Integer companyId);

    List<StatusCount> getOrderAmountStat(@Param("companyId")Integer companyId,
                                         @Param("startDate")Date startDate,
                                         @Param("endDate")Date endDate);
}