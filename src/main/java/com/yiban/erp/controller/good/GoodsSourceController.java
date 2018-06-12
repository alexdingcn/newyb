package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.RepertoryInDetail;
import com.yiban.erp.entities.SellOrderDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.goods.GoodsSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @RequestMapping(value = "/goodsList", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> queryGoods(@AuthenticationPrincipal User user){
        List<Goods> goods = goodsSourceService.queryGoods(user.getCompanyId());
        JSONObject result = new JSONObject();
        result.put("goodsList",goods);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    /**
     * 根据商品获取商品的所有批次
     * @param goodId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getBatch", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getBatch(@RequestParam(value="goodId",required = false) Long goodId) throws Exception{
        logger.info("goodId {}",goodId);
        if(goodId==null){
            throw new BizException(ErrorCode.SHIP_SAVE_PARAMS_ERROR);
        }
        //Long goodId =goodsSourceService.getGoodIdByDetail(detailId);
        List<String> batchs = goodsSourceService.getBatch(goodId);
        JSONObject result = new JSONObject();
        result.put("goodId",goodId);
        result.put("data",batchs);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    /**
     * 查询 采购历史、库存、销售情况和在单数
     * @param goodId
     * @param batchCode
     * @return
     */
    @RequestMapping(value = "/searchInfo", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchInfo(@RequestParam(value="goodId",required = false) Long goodId,
                                             @RequestParam(value="batchCode",required = false) String batchCode){
        logger.info("searchInfo goodId {} , batchCode {}",goodId,batchCode);
        //庫存量
        Integer repertory = goodsSourceService.countStock(goodId,batchCode);
        /*在単數*/
        Integer onWayQuantity = goodsSourceService.countOnSell(goodId,batchCode);
        /*銷售情況*/
        List<SellOrderDetail> sell = goodsSourceService.getSell(goodId,batchCode);
        /*采?歷史*/
        List<RepertoryInDetail> Buy = goodsSourceService.getBuy(goodId,batchCode);
        JSONObject result = new JSONObject();
        result.put("repertory",repertory);
        result.put("onWayQuantity",onWayQuantity);
        result.put("sell",sell);
        result.put("Buy",Buy);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    /**
     * 商品销毁
     * @param user
     * @param goodId
     * @param batchCode
     * @return
     */
    @RequestMapping(value = "/destory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> destory(@AuthenticationPrincipal User user,
                                          @RequestParam(value="goodId",required = true ) Long goodId,
                                          @RequestParam(value="batchCode",required = false) String batchCode){
        String userName = user.getNickname();
        goodsSourceService.destory(goodId,batchCode, userName);
        return ResponseEntity.ok().build();
    }
}
