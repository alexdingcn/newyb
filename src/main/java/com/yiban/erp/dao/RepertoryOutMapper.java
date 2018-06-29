package com.yiban.erp.dao;

import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.RepertoryOutListReq;
import com.yiban.erp.entities.RepertoryOut;
import com.yiban.erp.entities.RepertoryOutList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepertoryOutMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepertoryOut record);

    RepertoryOut selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepertoryOut record);

    List<RepertoryOut> getList(ReceiveListReq listReq);
    List<RepertoryOut> getOutListById(@Param("ids") List<Long> listId);
    List<RepertoryOutList> getOutListDamage(@Param("id") Long id);
    List<RepertoryOutList> getOutListChange(@Param("id") Long id);
    int deleteOrder(@Param("id") Long id);
}