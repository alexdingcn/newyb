package com.yiban.erp.dao;

import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.entities.RepositoryOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepositoryOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepositoryOrder record);

    int insertSelective(RepositoryOrder record);

    RepositoryOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepositoryOrder record);

    int updateByPrimaryKey(RepositoryOrder record);

    List<RepositoryOrder> getList(ReceiveListReq listReq);


    RepositoryOrder getByBuyOrder(@Param("companyId") Integer companyId, @Param("buyOrderId") Long buyOrderId);
}