package com.yiban.erp.dao;

import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.RepertoryOutListReq;
import com.yiban.erp.entities.RepertoryOutDetail;
import com.yiban.erp.entities.RepertoryOutSider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepertoryOutDetailMapper {
    int deleteByPrimaryKey(Long id);
    int deleteByRepertoryOutId(@Param(value = "id")Long id);
    int insert(RepertoryOutDetail record);

    RepertoryOutDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryOutDetail record);

    int insertBatch(@Param("details") List<RepertoryOutDetail> outDetails);

    List<RepertoryOutDetail> getByOrderId(Long orderId);

    List<RepertoryOutDetail> getOutDetailList(RepertoryOutListReq reqlist);

    List<RepertoryOutSider> getUnchecked(@Param("companyId") int companyId,
                                         @Param("refType") String type);

    /**
     * 获取某一客户商品列表中的最近一次销售价
     * @param customerId
     * @param goodsIdList
     * @return
     */
    List<CurrentBalanceResp> getLastBuyPrice(@Param("customerId") Long customerId,
                                             @Param("goodsIdList") List<Long> goodsIdList);
}