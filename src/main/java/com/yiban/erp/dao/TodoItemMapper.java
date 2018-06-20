package com.yiban.erp.dao;

import com.yiban.erp.entities.TodoItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TodoItemMapper {

     List<TodoItems> getTodoItems(@Param(value = "companyId") int companyId);
     int addTodoItems(TodoItems todoItems);
    int updTodoItems(@Param(value = "id") int id);
}
