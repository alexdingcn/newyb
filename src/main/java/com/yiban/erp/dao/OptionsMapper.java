package com.yiban.erp.dao;

import com.yiban.erp.entities.Options;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OptionsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Options record);

    int insertSelective(Options record);

    Options selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Options record);

    int updateByPrimaryKey(Options record);

    List<Options> findByTypes(@Param("companyId") Integer companyId,
                              @Param("types") List<String> options);

    List<Options> getByIds(@Param("ids") Long[] ids);
}