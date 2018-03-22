package com.yiban.erp.dao;

import com.yiban.erp.entities.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    int insertSelective(Customer record);

    Customer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);

    Customer getCustomerDetailById(@Param("companyId") Integer companyId,
                                   @Param("customerId") Integer customerId);

    List<Customer> getByCategoryId(@Param("companyId") Integer companyId,
                                   @Param("categoryId") Integer categoryId);

    int selectAllCount(@Param("companyId") Integer companyId,
                       @Param("categoryId") Integer categoryId,
                       @Param("customerName") String customerName,
                       @Param("customerNo") String customerNo,
                       @Param("shorName") String shorName);

    List<Customer> selectAll(@Param("companyId") Integer companyId,
                             @Param("categoryId") Integer categoryId,
                             @Param("customerName") String customerName,
                             @Param("customerNo") String customerNo,
                             @Param("shorName") String shorName,
                             @Param("limit") Integer limit,
                             @Param("offset") Integer offset);

    List<Customer> searchLike(@Param("companyId") Integer companyId,
                                @Param("name") String name);
}