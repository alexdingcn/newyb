package com.yiban.erp.dao;

import com.yiban.erp.entities.PriceRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriceRuleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PriceRule record);

    int insertBatch(@Param("rules") List<PriceRule> newRule);

    PriceRule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PriceRule record);

    List<PriceRule> getByGoodsId(Long goodsId);

    PriceRule getByCustomer(@Param("goodsId") Long goodsId,
                            @Param("customerId") Long customerId);

    int removeCategorys(Long goodsId);


    int removeCustomer(@Param("goodsId") Long goodsId,
                       @Param("customerIds") List<Long> customerIds);
}