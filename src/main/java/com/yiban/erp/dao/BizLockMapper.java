package com.yiban.erp.dao;

import com.yiban.erp.entities.BizLock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizLockMapper {
    int insert(BizLock record);

    int deleteById(Integer id);

    int deleteByLockKey(String lockKey);

}