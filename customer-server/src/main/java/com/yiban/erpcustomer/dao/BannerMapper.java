package com.yiban.erpcustomer.dao;

import com.yiban.erpcustomer.dto.PagedQuery;
import com.yiban.erpcustomer.entities.Banner;
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

    Integer selectAllCount(PagedQuery query);

    List<Banner> selectAll(PagedQuery query);
}