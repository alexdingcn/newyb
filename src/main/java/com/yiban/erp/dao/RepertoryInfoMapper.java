package com.yiban.erp.dao;

import com.yiban.erp.entities.RepertoryInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepertoryInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RepertoryInfo record);

    int insertSelective(RepertoryInfo record);

    RepertoryInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RepertoryInfo record);

    int updateByPrimaryKey(RepertoryInfo record);

    Integer getDetailListCount(@Param("companyId") Integer companyId,
                               @Param("warehouseId") Integer warehouseId,
                               @Param("saleEnable") Boolean saleEnable,
                               @Param("goodId") Long goodId,
                               @Param("goodSearch") String goodSearch,
                               @Param("factoryId") Integer factoryId);

    List<RepertoryInfo> getDetailList(@Param("companyId") Integer companyId,
                                      @Param("warehouseId") Integer warehouseId,
                                      @Param("saleEnable") Boolean saleEnable,
                                      @Param("goodId") Long goodId,
                                      @Param("goodSearch") String goodSearch,
                                      @Param("factoryId") Integer factoryId,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    List<RepertoryInfo> getByWarehouseIdAndGoodIds(@Param("warehouseId") Integer warehouseId,
                                                   @Param("goodIdList") List<Long> goodsIdList);
}