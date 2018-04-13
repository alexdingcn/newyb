package com.yiban.erp.dao;

import com.yiban.erp.entities.UserRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRouteMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoute record);

    int insertSelective(UserRoute record);

    UserRoute selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRoute record);

    int updateByPrimaryKey(UserRoute record);

    List<UserRoute> getByUserId(Long userId);

    int deleteByIdList(@Param("userId") Long userId,
                       @Param("ids") List<Long> ids);
}