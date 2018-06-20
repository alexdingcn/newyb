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

    /**
     *下面的方法为商品养护待办事项
     */
    int getTodoByGoodId(@Param(value = "refId") long id);
    int updTodoByGoodId(TodoItems todoItems);
    int addTodoByGoodId(TodoItems todoItems);
}
