package com.yiban.erp.dao;

import com.yiban.erp.entities.MessageOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageOptionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MessageOption record);

    int insertSelective(MessageOption record);

    MessageOption selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageOption record);

    int updateByPrimaryKey(MessageOption record);

    List<MessageOption> getByMessageId(Long messageId);
}