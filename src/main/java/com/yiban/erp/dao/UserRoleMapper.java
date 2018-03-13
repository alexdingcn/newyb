package com.yiban.erp.dao;

import com.yiban.erp.entities.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    List<UserRole> findByUserId(@Param("userId") Long userId);

    List<UserRole> findByRoles(@Param("roleList") List<String> strings, @Param("companyId") Integer companyId);

    int replaceInto(UserRole role);
}