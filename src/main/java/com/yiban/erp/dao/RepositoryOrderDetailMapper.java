package com.yiban.erp.dao;

import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.entities.RepositoryOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepositoryOrderDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepositoryOrderDetail record);

    int insertSelective(RepositoryOrderDetail record);

    RepositoryOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepositoryOrderDetail record);

    int updateByPrimaryKey(RepositoryOrderDetail record);

    int deleteByOrderId(Long id);

    int insertBatch(@Param("details") List<RepositoryOrderDetail> details);

    List<RepositoryOrderDetail> getByOrderIdList(@Param("orderIdList") List<Long> orderIdList);

    List<RepositoryOrderDetail> getByOrderId(Long orderId);

    int setCheckByOrder(ReceiveSetReq setReq); //一次验收一笔订单

    int setCheckByDetail(ReceiveSetReq setReq); //一次验收一笔订单详情

    int setUnCheckByOrder(ReceiveSetReq setReq); //一次取消验收一笔订单详情

    int setUnCheckByDetail(ReceiveSetReq setReq); //一次取消验收一笔订单详情
}