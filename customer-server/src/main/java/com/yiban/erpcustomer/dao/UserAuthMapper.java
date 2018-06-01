package com.yiban.erpcustomer.dao;

import com.yiban.erpcustomer.entities.UserAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface UserAuthMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserAuth record);

    int insertSelective(UserAuth record);

    UserAuth selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserAuth record);

    int updateByPrimaryKey(UserAuth record);

    UserAuth findByIdentifier(@Param("identifier") String findByIdentifier,
                              @Param("type") String type);

    int updateAuthIdentifier(@Param("userId") Long id,
                             @Param("type") String type,
                             @Param("value") String value,
                             @Param("updatedBy") String updatedBy,
                             @Param("updatedTime") Date updatedTime);

    int updatePassword(@Param("userId") Long userId,
                       @Param("credential") String credential,
                       @Param("updatedBy") String updatedBy,
                       @Param("updatedTime") Date updatedTime);

    int deleteByUserId(Long userId);
}