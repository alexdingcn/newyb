package com.yiban.erp.dao;

import com.yiban.erp.dto.FinancialQuery;
import com.yiban.erp.entities.FinancialPrePaid;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface FinancialPrePaidMapper {

    int insert(FinancialPrePaid record);

    FinancialPrePaid selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FinancialPrePaid record);

    List<FinancialPrePaid> getSearchList(FinancialQuery query);

    Integer getSearchListCount(FinancialQuery query);

}