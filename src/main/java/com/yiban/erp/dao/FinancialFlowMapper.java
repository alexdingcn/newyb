package com.yiban.erp.dao;

import com.yiban.erp.dto.FinancialQuery;
import com.yiban.erp.entities.FinancialFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FinancialFlowMapper {

    int insert(FinancialFlow record);

    FinancialFlow selectByPrimaryKey(Long id);

    FinancialFlow getIncludeOptionById(Long id);

    FinancialFlow getByFinancialBizNo(String bizNo);

    int updateByPrimaryKeySelective(FinancialFlow record);

    List<FinancialFlow> getByRefId(@Param("bizRefId") Long bizRefId,
                                   @Param("bizType") String bizType);

    List<FinancialFlow> getByRefNo(String bizRefNo);

    List<FinancialFlow> getFlowList(FinancialQuery query);

    Integer getFlowListCount(FinancialQuery query);


}