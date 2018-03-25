package com.yiban.erp.dao;

import com.yiban.erp.entities.SellReviewOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SellReviewOptionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SellReviewOption record);

    int insertSelective(SellReviewOption record);

    SellReviewOption selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellReviewOption record);

    int updateByPrimaryKey(SellReviewOption record);

    List<SellReviewOption> getByDetailIdList(@Param("detailIdList") List<Long> detailIdList,
                                             @Param("reviewType") String reviewType);

    int deleteByDetailIdList(@Param("detailIdList") List<Long> detailIdList,
                             @Param("reviewType") String reviewType);

    int replace(@Param("recordList") List<SellReviewOption> newReviewRecordList);

    List<Long> getUnCheckDetailIdList(@Param("sellOrderId") Long sellOrderId,
                                      @Param("reviewType") String reviewType);
}