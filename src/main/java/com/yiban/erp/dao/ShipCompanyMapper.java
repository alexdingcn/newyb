package com.yiban.erp.dao;

import com.yiban.erp.entities.ShipCompany;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShipCompanyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShipCompany record);

    int insertSelective(ShipCompany record);

    ShipCompany selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShipCompany record);

    int updateByPrimaryKey(ShipCompany record);

    List<ShipCompany> getByCompanyId(Integer companyId);
}