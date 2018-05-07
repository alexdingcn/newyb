package com.yiban.erp.controller.sell;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.entities.SellOrderBack;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.sell.SellOrderBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sell/back")
public class SellOrderBackController {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderBackController.class);

    @Autowired
    private SellOrderBackService sellOrderBackService;


    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody SellOrderBack orderBack,
                                      @AuthenticationPrincipal User user) throws Exception {

        logger.info("user:{} request add sell order back by:{}", user.getId(), JSON.toJSONString(orderBack));
        sellOrderBackService.add(orderBack, user);
        return ResponseEntity.ok().build();
    }

}
