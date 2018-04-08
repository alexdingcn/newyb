package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryCheckPart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepertoryCheckPartMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryCheckPart record);

    int insertSelective(RepertoryCheckPart record);

    RepertoryCheckPart selectByPrimaryKey(Long id);

    List<RepertoryCheckPart> selectByCheckOrderId(Long checkOrderId);

    int updateByPrimaryKeySelective(RepertoryCheckPart record);

    int updateByPrimaryKey(RepertoryCheckPart record);
}
