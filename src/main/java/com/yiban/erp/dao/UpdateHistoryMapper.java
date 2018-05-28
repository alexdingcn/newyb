package com.yiban.erp.dao;

import com.yiban.erp.entities.UpdateHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UpdateHistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UpdateHistory record);

    int insertSelective(UpdateHistory record);

    UpdateHistory selectByPrimaryKey(Integer id);

    List<UpdateHistory> selectFromLastId(@Param("companyId") Integer companyId,
                                         @Param("id") Integer id);

    int updateByPrimaryKeySelective(UpdateHistory record);

    int updateByPrimaryKey(UpdateHistory record);
}