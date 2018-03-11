package com.yiban.erp.dao;

import com.yiban.erp.entities.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FileInfo record);

    int insertSelective(FileInfo record);

    FileInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileInfo record);

    int updateByPrimaryKey(FileInfo record);

    /**
     * 排除掉了删除状态的
     * @param companyId
     * @param fileNo
     * @return
     */
    FileInfo getByFileNo(@Param("companyId") Integer companyId,
                         @Param("fileNo") String fileNo);
}