package com.yiban.erp.dao;

import com.yiban.erp.entities.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

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

    FileInfo getFileInfoDetailById(Integer id);

    Integer getByFileTypeAndNameCount(@Param("companyId") Integer companyId,
                                      @Param("fileType") String fileType,
                                      @Param("fileName") String fileName,
                                      @Param("fileNo") String fileNo);

    List<FileInfo> getByFileTypeAndName(@Param("companyId") Integer companyId,
                                        @Param("fileType") String fileType,
                                        @Param("fileName") String fileName,
                                        @Param("fileNo") String fileNo,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    int removeByIds(@Param("ids") List<Integer> ids,
                    @Param("updateBy") String updateBy,
                    @Param("updateTime") Date updateTime);



}