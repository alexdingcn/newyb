package com.yiban.erp.dao;

import com.yiban.erp.entities.FileType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FileType record);

    int insertSelective(FileType record);

    FileType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileType record);

    int updateByPrimaryKey(FileType record);

    List<FileType> getAll(Integer companyId);
}