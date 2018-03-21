package com.yiban.erp.dao;

import com.yiban.erp.entities.ShipCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShipCompanyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShipCompany record);

    int insertSelective(ShipCompany record);

    ShipCompany selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShipCompany record);

    int updateByPrimaryKey(ShipCompany record);

    Integer getListCount(@Param("companyId") Integer companyId,
                              @Param("name") String name,
                              @Param("license") String license);

    List<ShipCompany> getList(@Param("companyId") Integer companyId,
                                     @Param("name") String name,
                                     @Param("license") String license,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
}