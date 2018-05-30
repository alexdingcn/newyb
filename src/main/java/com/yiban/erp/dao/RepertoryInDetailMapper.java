package com.yiban.erp.dao;

import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.dto.RepertoryInListReq;
import com.yiban.erp.entities.RepertoryInDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepertoryInDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryInDetail record);

    RepertoryInDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryInDetail record);

    int deleteByOrderId(Long id);

    int insertBatch(@Param("details") List<RepertoryInDetail> details);

    List<RepertoryInDetail> getByOrderId(Long orderId);

    List<RepertoryInDetail>   getInDetailList(RepertoryInListReq reqlist);

    int setCheckByOrder(ReceiveSetReq setReq); //一次验收一笔订单

    int setCheckByDetail(ReceiveSetReq setReq); //一次验收一笔订单详情

    int setUnCheckByOrder(ReceiveSetReq setReq); //一次取消验收一笔订单详情

    int setUnCheckByDetail(ReceiveSetReq setReq); //一次取消验收一笔订单详情

}