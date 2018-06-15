package com.yiban.erp.dao;

import com.yiban.erp.entities.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User findUserByNameOrMobile(@Param("identifier") String identifier);

    User findUserByNickName(String nickname);

    User findUserByMobile(String mobile);

    List<User> selectAll(Integer companyId);

    User getDetailById(Long userId);

    int updateMobile(User oldUser);

    int updateNickName(User oldUser);

    int updateUserStatusToDelete(User updateUser);
}