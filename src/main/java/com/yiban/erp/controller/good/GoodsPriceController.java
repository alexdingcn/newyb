package com.yiban.erp.controller.good;


import com.alibaba.fastjson.JSON;
import com.yiban.erp.dto.PriceUpdateReq;
import com.yiban.erp.entities.GoodsDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.goods.GoodsPriceService;
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

import java.util.List;

@RestController
@RequestMapping("/goods/price")
public class GoodsPriceController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsPriceController.class);

    @Autowired
    private GoodsPriceService goodsPriceService;


    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateBasePrice(@RequestBody PriceUpdateReq updateReq,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request update goods price by request params:{}", user.getId(), JSON.toJSONString(updateReq));
        List<GoodsDetail> details = goodsPriceService.updateBasePrice(updateReq, user);
        return ResponseEntity.ok().body(JSON.toJSONString(details));
    }


}
