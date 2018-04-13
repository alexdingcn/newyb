package com.yiban.erp.dao;

import com.yiban.erp.entities.UserRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRouteMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoute record);

    UserRoute selectByPrimaryKey(Long id);


    List<UserRoute> getByUserId(Long userId);

    int insertBatch(@Param("list") List<UserRoute> newUserRoute);

    int deleteByUserId(Long id);
}