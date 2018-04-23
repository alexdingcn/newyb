package com.yiban.erp.dao;

import com.yiban.erp.dto.SellOrderAllAction;
import com.yiban.erp.dto.SellReviewOrderQuery;
import com.yiban.erp.entities.SellOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface SellOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrder record);

    SellOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrder record);

    Integer getListCount(@Param("companyId") Integer companyId,
                            @Param("customerId") Integer customerId,
                            @Param("saleId") Long saleId,
                            @Param("refNo") String refNo,
                            @Param("status") String status,
                         @Param("createOrderDate") Date createOrderDate);

    List<SellOrder> getList(@Param("companyId") Integer companyId,
                            @Param("customerId") Integer customerId,
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
}