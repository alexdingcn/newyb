package com.yiban.erp.dao;

import com.yiban.erp.entities.GoodsAttribute;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsAttributeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsAttribute record);

    GoodsAttribute selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsAttribute record);

    List<GoodsAttribute> getByCompanyId(Integer companyId);

    boolean isUsedAttribute(Long id);


}