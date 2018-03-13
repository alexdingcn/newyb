package com.yiban.erp.dao;

import com.yiban.erp.entities.FileUpload;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileUploadMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FileUpload record);

    int insertSelective(FileUpload record);

    FileUpload selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileUpload record);

    int updateByPrimaryKey(FileUpload record);
}