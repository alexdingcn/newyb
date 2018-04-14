package com.yiban.erp.dao;

import com.yiban.erp.entities.MessageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MessageInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MessageInfo record);

    int insertSelective(MessageInfo record);

    MessageInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageInfo record);

    List<MessageInfo> query(@Param("companyId") Integer companyId,
                            @Param("roleTypeList") List<String> roleTypeList,
                            @Param("startTime") Date startTime,
                            @Param("endTime") Date endTime);
}