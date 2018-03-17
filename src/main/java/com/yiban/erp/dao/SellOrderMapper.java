package com.yiban.erp.dao;

import com.yiban.erp.entities.SellOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SellOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellOrder record);

    int insertSelective(SellOrder record);

    SellOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellOrder record);

    int updateByPrimaryKey(SellOrder record);

    Integer getListCount(@Param("companyId") Integer companyId,
                            @Param("customerId") Integer customerId,
                            @Param("salerId") Integer salerId,
                            @Param("refNo") String refNo,
                            @Param("status") String status);

    List<SellOrder> getList(@Param("companyId") Integer companyId,
                            @Param("customerId") Integer customerId,
                            @Param("salerId") Integer salerId,
                            @Param("refNo") String refNo,
                            @Param("status") String status,
                            @Param("limit") int limit,
                            @Param("offset") int offset);
}