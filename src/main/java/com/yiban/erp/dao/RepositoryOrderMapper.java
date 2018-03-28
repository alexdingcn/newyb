package com.yiban.erp.dao;

import com.yiban.erp.entities.RepositoryOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepositoryOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepositoryOrder record);

    int insertSelective(RepositoryOrder record);

    RepositoryOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepositoryOrder record);

    int updateByPrimaryKey(RepositoryOrder record);
}