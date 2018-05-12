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
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody GoodsSpecForm form,
                                       @AuthenticationPrincipal User user) throws Exception{
        logger.info("user:{} request save GoodsSpecForm :{}", user.getId(), JSON.toJSONString(form));
        goodsSpecService.save(form, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/remove/{specId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Long specId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove goods spec id:{}", user.getId(), specId);
        goodsSpecService.remove(specId);
        return ResponseEntity.ok().build();
    }



}
