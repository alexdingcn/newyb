package com.yiban.erp.dao;

import com.yiban.erp.entities.RepositoryOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepositoryOrderDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepositoryOrderDetail record);

    int insertSelective(RepositoryOrderDetail record);

    RepositoryOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepositoryOrderDetail record);

    int updateByPrimaryKey(RepositoryOrderDetail record);

    int deleteByOrderId(Long id);

    int insertBatch(@Param("details") List<RepositoryOrderDetail> details);

    List<RepositoryOrderDetail> getByOrderIdList(@Param("orderIdList") List<Long> orderIdList);
}