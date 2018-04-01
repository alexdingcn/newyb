package com.yiban.erp.executor.dao;

import com.yiban.erp.executor.entities.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getUser(String nickname);

}
