package com.yiban.erp.dao;

import com.yiban.erp.dto.GoodsBrandQuery;
import com.yiban.erp.entities.GoodsBrand;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsBrandMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsBrand record);

    GoodsBrand selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsBrand record);

    int getBrandCount(GoodsBrandQuery query);

    List<GoodsBrand> getBrands(GoodsBrandQuery query);

    boolean isBrandUsed(Long brandId);
}