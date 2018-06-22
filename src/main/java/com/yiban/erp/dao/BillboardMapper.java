package com.yiban.erp.dao;

import com.yiban.erp.entities.Billboard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BillboardMapper {

    int insert(Billboard billboard);
    List<Billboard> getList(@Param("companyId") Integer companyId);
    int update(Billboard billboard);
    int delete(@Param("id") int id);
    int sort(@Param("id") int id,
             @Param("number") int number);
    List<Billboard> display(@Param("companyId") Integer companyId);
}
