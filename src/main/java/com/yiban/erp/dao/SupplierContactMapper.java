package com.yiban.erp.dao;

import com.yiban.erp.entities.SupplierContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplierContactMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SupplierContact record);

    SupplierContact selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SupplierContact record);

    List<SupplierContact> getSupplierContacts(@Param("supplierId") Long supplierId);
}