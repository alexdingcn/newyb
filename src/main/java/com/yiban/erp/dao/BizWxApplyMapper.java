package com.yiban.erp.dao;

import com.yiban.erp.entities.BizWxApply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizWxApplyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BizWxApply record);

    int insertSelective(BizWxApply record);

    BizWxApply selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BizWxApply record);

    int updateByPrimaryKey(BizWxApply record);
}