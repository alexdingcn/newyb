package com.yiban.erp.controller.home;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.dao.TodoItemMapper;
import com.yiban.erp.dao.UpdateHistoryMapper;
import com.yiban.erp.entities.StatusCount;
import com.yiban.erp.entities.TodoItems;
import com.yiban.erp.entities.UpdateHistory;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.ErrorCode;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")

public class HomeController {
    @Autowired
    private SellOrderMapper sellOrderMapper;
    @Autowired
    private UpdateHistoryMapper updateHistoryMapper;
    @Autowired
    private TodoItemMapper todoItemMapper;

    @RequestMapping(value = "/updates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getUpdates(@RequestParam(value = "lt", required = false) Integer lastIndex,
                                              @AuthenticationPrincipal User user) {
        List<UpdateHistory> histories = updateHistoryMapper.selectFromLastId(user.getCompanyId(), lastIndex);
        return ResponseEntity.ok().body(JSON.toJSONString(histories));
    }

    @RequestMapping(value = "/orderstats", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getOrderStats(@AuthenticationPrincipal User user) {

        List<StatusCount> counts = sellOrderMapper.getOrderStatusStat(user.getCompanyId());

        return ResponseEntity.ok().body(JSON.toJSONString(counts));
    }

    @RequestMapping(value = "/amountstats", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getAmountStat(@AuthenticationPrincipal User user, @RequestBody Map requestMap) {
        Date startDate = DateUtils.parseDate((String) requestMap.getOrDefault("startDate", ""), new String[]{"yyyy-MM-dd"});
        Date endDate = DateUtils.parseDate((String) requestMap.getOrDefault("endDate", ""), new String[]{"yyyy-MM-dd"});

        List<StatusCount> counts = sellOrderMapper.getOrderAmountStat(user.getCompanyId(), startDate, endDate);

        return ResponseEntity.ok().body(JSON.toJSONString(counts));
    }

    @RequestMapping(value = "/goodsstats", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getGoodsStats(@AuthenticationPrincipal User user, @RequestBody Map requestMap) {
        Date startDate = DateUtils.parseDate((String) requestMap.getOrDefault("startDate", ""), new String[]{"yyyy-MM-dd"});
        Date endDate = DateUtils.parseDate((String) requestMap.getOrDefault("endDate", ""), new String[]{"yyyy-MM-dd"});

        List<StatusCount> counts = sellOrderMapper.getGoodsAmountStat(user.getCompanyId(), startDate, endDate);

        return ResponseEntity.ok().body(JSON.toJSONString(counts));
    }

    /**
     * 获得待办事项列表
     * @param user
     * @return
     */
    @RequestMapping(value = "/getTodoItems", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getTodoItems(@AuthenticationPrincipal User user){
        int companyId = user.getCompanyId();
        List<TodoItems> results = todoItemMapper.getTodoItems(companyId);
        return ResponseEntity.ok().body(JSON.toJSONString(results));
    }

    /**
     * 添加待办事项
     * @param user
     * @param todoItems
     * @return
     */
    @RequestMapping(value = "/addTodoItems", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> addTodoItems(@AuthenticationPrincipal User user,
                                                @RequestBody TodoItems todoItems){
        todoItems.setCompanyId(user.getCompanyId());
        todoItems.setCreateBy(user.getNickname());
        int i = todoItemMapper.addTodoItems(todoItems);
        if(i>0){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().body(ErrorCode.FAILED_INSERT_FROM_DB.toString());
        }
    }

    @RequestMapping(value = "/updateTodoItems/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> updateTodoItems(@PathVariable(value="id") int id){
       int i = todoItemMapper.updTodoItems(id);
       if(i > 0){
           return ResponseEntity.ok().build();
       }else{
           return ResponseEntity.badRequest().body(ErrorCode.FAILED_UPDATE_FROM_DB.toString());
       }
    }
}
