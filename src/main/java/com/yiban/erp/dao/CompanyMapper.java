package com.yiban.erp.dao;

import com.yiban.erp.entities.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface CompanyMapper {

    int insert(Company record);

    Company selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Company record);

    Company getByLicense(String license);

    int updateAccountAmount(@Param("id") Integer id,
                            @Param("accountAmount")BigDecimal accountAmount,
                            @Param("inAmount") BigDecimal inAmount,
                            @Param("outAmount") BigDecimal outAmount);
}