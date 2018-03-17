package com.yiban.erp.controller.sell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.sell.SellOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/sell")
public class SellOrderController {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderController.class);

    @Autowired
    private SellOrderService sellOrderService;
    @Autowired
    private SellOrderMapper sellOrderMapper;

    @RequestMapping(value = "/order/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrderList(@RequestParam(name = "customerId", required = false) Integer customerId,
                                               @RequestParam(name = "salerId", required = false) Integer salerId,
                                               @RequestParam(name = "refNo", required = false) String refNo,
                                               @RequestParam(name = "status", required = false) String status,
                                               @RequestParam(name = "createOrderDate", required = false) Date createOrderDate,
                                               @RequestParam(name = "page", required = false) Integer page,
                                               @RequestParam(name = "size", required = false) Integer size,
                                               @AuthenticationPrincipal User user) throws Exception {
        List<SellOrder> sellOrders = sellOrderService.getList(user.getCompanyId(), customerId, salerId, refNo, status, createOrderDate, page, size);
        JSONObject result = new JSONObject();
        if (!sellOrders.isEmpty()) {
            Integer count = sellOrderMapper.getListCount(user.getCompanyId(), customerId, salerId, refNo, status, createOrderDate);
            result.put("count", count == null ? 0 : count);
            result.put("data", sellOrders);
        }else {
            result.put("count", 0);
            result.put("data", new ArrayList<SellOrder>());
        }
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/order/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderAdd(@RequestBody SellOrder sellOrder,
                                           @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request add sell order:{}", user.getId(), JSON.toJSONString(sellOrder));
        SellOrder result = sellOrderService.orderAdd(user, sellOrder);
        logger.info("user:{} success add a sell order:{}", user.getId(), result.getId());
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/order/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderUpdate(@RequestBody SellOrder sellOrder,
                                              @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request update sell order:{}", user.getId(), JSON.toJSONString(sellOrder));
        SellOrder result = sellOrderService.orderUpdate(user, sellOrder);
        logger.debug("user:{} success update a sell order:{}", user.getId(), result.getId());
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }



}
