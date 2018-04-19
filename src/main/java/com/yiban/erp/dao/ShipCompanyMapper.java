package com.yiban.erp.dao;

import com.yiban.erp.entities.ShipCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShipCompanyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShipCompany record);

    ShipCompany selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShipCompany record);

    Integer getListCount(@Param("companyId") Integer companyId,
                         @Param("search") String search);

    List<ShipCompany> getList(@Param("companyId") Integer companyId,
                             @Param("search") String search,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);
}