package com.yiban.erp.controller.warehouse;


import com.yiban.erp.service.warehouse.GoodsCareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/goodsCare")
public class GoodsCareController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsCareController.class);

    @Autowired
    private GoodsCareService goodsCareService;

    @RequestMapping(value = "/goodslist", method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> queryGoods(@PathVariable("searchGoodsVal") String querstr){
        goodsCareService.queryGoods(querstr);

        return null;
    }
}
