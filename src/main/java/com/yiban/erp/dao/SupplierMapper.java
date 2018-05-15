package com.yiban.erp.dao;

import com.yiban.erp.entities.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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

    int updateAccountAmount(@Param("id") Long id,
                            @Param("accountAmount")BigDecimal accountAmount);

    int selectAllCount(@Param("companyId") Integer companyId,
                       @Param("search") String search);

    List<Supplier> selectAllPaged(@Param("companyId") Integer companyId,
                             @Param("search") String search,
                             @Param("limit") Integer limit,
                             @Param("offset") Integer offset);
}