package com.yiban.erp.dao;

import com.yiban.erp.entities.SellOrderBackDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SellOrderBackDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrderBackDetail record);

    int insertBatch(@Param("details") List<SellOrderBackDetail> details);

    SellOrderBackDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrderBackDetail record);

    int updateAlreadyBackQuantity(Long backOrderId);
}