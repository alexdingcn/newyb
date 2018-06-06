package com.yiban.erp.dao;

import com.yiban.erp.dto.BuyBackReq;
import com.yiban.erp.entities.RepertoryInBackDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface RepertoryInBackDetailMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByInBackId(Long inBackId);

    int insert(RepertoryInBackDetail record);

    RepertoryInBackDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryInBackDetail record);

    int insertBatch(@Param("details") List<RepertoryInBackDetail> details);

    List<RepertoryInBackDetail> getDetailByBackId(Long backId);

    int updateCheckStatus(BuyBackReq buyBackReq);

    int cancelCheckStatus(@Param("backId") Long backId,
                          @Param("updatedBy") String updatedBy,
                          @Param("updatedTime") Date updatedTime);
}