package com.yiban.erp.dao;

import com.yiban.erp.dto.SellOrderAllAction;
import com.yiban.erp.dto.SellOrderQuery;
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

    Integer getListCount(SellOrderQuery query);

    List<SellOrder> getList(SellOrderQuery query);

    List<SellOrder> getAllList(SellOrderAllAction params);

    Integer getAllCount(SellOrderAllAction params);

    List<SellOrder> getListById(@Param("idList") List<Long> idList);

    SellOrder getReviewDetailById(Long id);

    List<SellOrder> getReviewOrderList(SellReviewOrderQuery query);
}