package com.yiban.erp.dao;

import com.yiban.erp.entities.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplierMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Supplier record);

    Supplier selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Supplier record);

    List<Supplier> selectAll(Integer companyId);

    List<Supplier> searchByNameOrContact(@Param("companyId") Integer companyId,
                                         @Param("searchStr") String searchStr);
}