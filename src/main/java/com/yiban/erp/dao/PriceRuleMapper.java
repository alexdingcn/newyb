package com.yiban.erp.dao;

import com.yiban.erp.entities.PriceRule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriceRuleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PriceRule record);

    PriceRule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PriceRule record);

}