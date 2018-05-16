package com.yiban.erp.dao;

import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.GoodsDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface GoodsDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsDetail record);

    GoodsDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsDetail record);

    int insertBatch(@Param("details") List<GoodsDetail> details);

    //修改到DELETE 状态
    int deleteByGoodsInfoId(@Param("goodsInfoId") Long goodsInfoId,
                            @Param("updatedBy") String updatedBy,
                            @Param("updatedTime") Date updatedTime);

    List<GoodsDetail> getByGoodsInfoId(Long id);


}