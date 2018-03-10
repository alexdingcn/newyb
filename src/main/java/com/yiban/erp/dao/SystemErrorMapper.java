package com.yiban.erp.dao;

import com.yiban.erp.entities.SystemError;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemErrorMapper {
    int insert(SystemError record);

    int update(SystemError record);

    List<SystemError> getAll();

}