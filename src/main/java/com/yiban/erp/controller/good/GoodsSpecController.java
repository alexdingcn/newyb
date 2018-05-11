package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dto.GoodsSpecForm;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.goods.GoodsSpecService;
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
@RequestMapping("/goods/spec")
public class GoodsSpecController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsSpecController.class);

    @Autowired
    private GoodsSpecService goodsSpecService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user) throws Exception {
        List<GoodsSpecForm> form = goodsSpecService.getListForm(user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(form));
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody GoodsSpecForm form,
                                       @AuthenticationPrincipal User user) throws Exception{
        logger.info("user:{} request save GoodsSpecForm :{}", user.getId(), JSON.toJSONString(form));
        goodsSpecService.save(form);
        return ResponseEntity.ok().build();
    }



}
