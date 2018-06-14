package com.yiban.erp.dao;

import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.entities.RepertoryOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface RepertoryInMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryIn record);

    RepertoryIn selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryIn record);

    List<RepertoryIn> getList(ReceiveListReq listReq);

    List<RepertoryIn> getInListById(@Param("ids") List<Long> listId);

    RepertoryIn getByRefOrder(@Param("companyId") Integer companyId,
                              @Param("refType") String refType,
                              @Param("refOrderId") Long refOrderId);

    int setCheckStatus(@Param("id") Long repositoryOrderId,
                       @Param("status") String name,
                       @Param("updateBy") String nickname,
                       @Param("updateTime") Date date);

    RepertoryIn getByIdWithSupplierInfo(Long id);

    RepertoryIn getOrderView(Long orderId);
}