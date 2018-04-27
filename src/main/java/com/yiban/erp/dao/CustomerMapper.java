package com.yiban.erp.dao;

import com.yiban.erp.entities.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper
public interface CustomerMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByIdList(@Param("ids") List<Integer> ids,
                       @Param("companyId") Integer companyId,
                       @Param("updateBy") String updateBy,
                       @Param("updateTime")Date updateTime);

    int insert(Customer record);

    Customer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Customer record);

    Customer getCustomerDetailById(@Param("companyId") Integer companyId,
                                   @Param("customerId") Long customerId);

    List<Customer> getByCategoryId(@Param("companyId") Integer companyId,
                                   @Param("categoryId") Integer categoryId);

    int selectAllCount(@Param("companyId") Integer companyId,
                       @Param("categoryId") Integer categoryId,
                       @Param("customerName") String customerName,
                       @Param("customerNo") String customerNo,
                       @Param("search") String search);

    List<Customer> selectAll(@Param("companyId") Integer companyId,
                             @Param("categoryId") Integer categoryId,
                             @Param("customerName") String customerName,
                             @Param("customerNo") String customerNo,
                             @Param("search") String search,
                             @Param("limit") Integer limit,
                             @Param("offset") Integer offset);

    List<Customer> searchLike(@Param("companyId") Integer companyId,
                                @Param("name") String name);

    int updateAccountAmount(@Param("id") Long id,
                            @Param("accountAmount")BigDecimal accountAmount);
}