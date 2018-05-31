package com.yiban.erp.dao;

import com.yiban.erp.dto.PagedQuery;
import com.yiban.erp.entities.Banner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BannerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Banner record);

    int insertSelective(Banner record);

    Banner selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Banner record);

    int updateByPrimaryKey(Banner record);

    List<Banner> selectAll(PagedQuery query);

    Integer selectAllCount(PagedQuery query);
}