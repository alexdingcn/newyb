package com.yiban.erp.dao;

import com.yiban.erp.dto.FinancialQuery;
import com.yiban.erp.entities.FinancialPreRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface FinancialPreReceiveMapper {

    int insert(FinancialPreRecord record);

    FinancialPreRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FinancialPreRecord record);

    List<FinancialPreRecord> getSearchList(FinancialQuery query);

    Integer getSearchListCount(FinancialQuery query);

    /**
     * 根据流水号获取。流水号与预付款记录一一对应
     * @param flowId
     * @return
     */
    FinancialPreRecord getByFlowId(Long flowId);

    int setStatusByFlowId(@Param("flowId") Long flowId,
                          @Param("status") String status,
                          @Param("updatedBy") String updatedBy,
                          @Param("updatedTime") Date updatedTime);

}