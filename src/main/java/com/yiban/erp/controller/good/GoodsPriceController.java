package com.yiban.erp.controller.good;


import com.alibaba.fastjson.JSON;
import com.yiban.erp.dto.CustomerCategoryPrice;
import com.yiban.erp.dto.PriceQuery;
import com.yiban.erp.dto.PriceUpdateReq;
import com.yiban.erp.dto.SavePriceReq;
import com.yiban.erp.entities.GoodsDetail;
import com.yiban.erp.entities.PriceRule;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.goods.GoodsPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/category/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> customerCategoryPriceList(@RequestBody PriceQuery query,
                                                            @AuthenticationPrincipal User user) {
        List<CustomerCategoryPrice> categoryPrices = goodsPriceService.getCustomerCategoryPrice(query.getGoodsDetailIds(), user);
        return ResponseEntity.ok().body(JSON.toJSONString(categoryPrices));
    }

    @RequestMapping(value = "/customer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> customerPrice(@RequestParam("goodsId") Long goodsId,
                                                @RequestParam("customerId") Long customerId) {
        PriceRule priceRule = goodsPriceService.getCustomerPrice(goodsId, customerId);
        return ResponseEntity.ok().body(JSON.toJSONString(priceRule));
    }

    @RequestMapping(value = "/customer/price", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> customerPriceByGoodsList(@RequestBody PriceQuery query,
                                                         @AuthenticationPrincipal User user) throws Exception {
        //根据商品ID，和客户ID，类型，获取客户的配置价格，如果客户不存在，返回的是商品的配置价格
        logger.info("request params: {}", JSON.toJSONString(query));
        Map<Long, PriceRule> priceRuleMap = goodsPriceService.getCustomerPriceByGoodsList(query, user);
        return ResponseEntity.ok().body((JSON) JSON.toJSON(priceRuleMap));
    }

    @RequestMapping(value = "/category/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> categorySave(@RequestBody SavePriceReq priceReq,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save goods price params:{}", user.getId(), JSON.toJSONString(priceReq));
        goodsPriceService.categorySave(priceReq, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/customer/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> customerSave(@RequestBody SavePriceReq priceReq,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save customer price params:{}", user.getId(), JSON.toJSONString(priceReq));
        goodsPriceService.customerSave(priceReq, user);
        return ResponseEntity.ok().build();
    }

}
