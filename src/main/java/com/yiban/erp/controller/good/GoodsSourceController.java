package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.goods.GoodsSourceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods/source")
public class GoodsSourceController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsSourceController.class);

    @Autowired
    private GoodsSourceService goodsSourceService;

    @RequestMapping(value = "/getBatch", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getBatch(@RequestParam(value="goodId",required = false) Long goodId) throws Exception{
        logger.info("goodId {}",goodId);
        if(goodId==null){
            throw new BizException(ErrorCode.SHIP_SAVE_PARAMS_ERROR);
        }
       List<String> batchs = goodsSourceService.getBatch(goodId);
        return ResponseEntity.ok().body(JSON.toJSONString(batchs));

    }
}
