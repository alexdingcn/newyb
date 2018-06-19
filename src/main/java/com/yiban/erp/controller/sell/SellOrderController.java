package com.yiban.erp.controller.sell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.dto.SellOrderAllAction;
import com.yiban.erp.dto.SellOrderQuery;
import com.yiban.erp.dto.SellReviewAction;
import com.yiban.erp.dto.SellReviewOrderQuery;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.sell.SellOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sell")
public class SellOrderController {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderController.class);

    @Autowired
    private SellOrderService sellOrderService;
    @Autowired
    private SellOrderMapper sellOrderMapper;


    @RequestMapping(value = "/order/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrderList(@RequestBody SellOrderQuery query,
                                               @AuthenticationPrincipal User user) throws Exception {
        query.setCompanyId(user.getCompanyId());
        Integer count = sellOrderMapper.getListCount(query);
        JSONObject result = new JSONObject();
        if (count != null && count > 0) {
            List<SellOrder> sellOrders = sellOrderMapper.getList(query);
            result.put("count", count);
            result.put("data", sellOrders);
        }else {
            result.put("count", 0);
            result.put("data", new ArrayList<SellOrder>());
        }
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }


    @RequestMapping(value = "/order/validate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sellOrderValidate(@RequestBody SellOrder sellOrder,
                                                    @AuthenticationPrincipal User user) throws Exception {
        List<String> errorList = sellOrderService.orderValidate(sellOrder, user);
        return ResponseEntity.ok().body(JSON.toJSONString(errorList));
    }

    @RequestMapping(value = "/order/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderSave(@RequestBody SellOrder sellOrder,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save sell order info.", user.getId());
        sellOrderService.orderSave(user, sellOrder);
        logger.info("user:{} success save sell order info.", user.getId());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/remove/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderRemove(@PathVariable Long id,
                                              @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove sell order, id:{}", user.getId(), id);
        sellOrderService.removeSellOrder(user, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/detail/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailList(@RequestParam("sellOrderId") Long sellOrderId) throws Exception {
        List<SellOrderDetail> details = sellOrderService.getDetailList(sellOrderId);
        return ResponseEntity.ok().body(JSON.toJSONString(details, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/detail/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDetailHistory(@RequestParam("customerId") Long customerId,
                                                   @RequestParam("goodsId") Long goodsId,
                                                   @AuthenticationPrincipal User user) throws Exception {
        List<SellOrderDetail> details = sellOrderService.getDetailHistory(user.getCompanyId(), customerId, goodsId);
        return ResponseEntity.ok().body(JSON.toJSONString(details, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/detail/remove/{detailId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeDetail(@PathVariable Long detailId,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request to delete sell order detail by id:{}", user.getId(), detailId);
        int count = sellOrderService.removeSellOrderDetail(user, detailId);
        logger.info("user:{} success remove sell order detail count:{}", user, count);
        JSONObject result = new JSONObject();
        result.put("count", result);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/order/review/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reviewOrderList(@RequestBody SellReviewOrderQuery query,
                                                  @AuthenticationPrincipal User user) {
        query.setCompanyId(user.getCompanyId());
        List<SellOrder> result = sellOrderService.getReviewOrderList(query);
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/order/review/quality/check", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> qualityCheck(@RequestBody SellReviewAction reviewAction,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} submit sell order review, params:{}", user.getId(), JSON.toJSONString(reviewAction));
        Long sellOrderId = reviewAction.getSellOrderId();
        if (sellOrderId == null) {
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        sellOrderService.qualityCheck(user, reviewAction);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/review/quality/cancel/{sellOrderId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reviewCancel(@PathVariable Long sellOrderId,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request cancel review sellOrderId:{}", user.getId(), sellOrderId);
        if (sellOrderId == null) {
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        sellOrderService.qualityCheckCancel(user, sellOrderId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/review/sale/ok", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sellOrderCheckOk(@RequestBody String reqData,
                                                   @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} check sell order sale review ok. id:{}", user.getId(), reqData);
        JSONObject json = JSON.parseObject(reqData);
        Long sellOrderId = json.getLong("orderId");
        sellOrderService.sellOrderCheckOk(user, sellOrderId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/review/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reviewDetail(@RequestParam("orderId") Long orderId) throws Exception {
        SellOrder order = sellOrderService.reviewDetail(orderId);
        if (order == null) {
            logger.warn("get order detail fail by id:{}", orderId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }else {
            return ResponseEntity.ok().body(JSON.toJSONString(order, SerializerFeature.DisableCircularReferenceDetect));
        }
    }

    @RequestMapping(value = "/order/ship/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getShipList(@RequestParam("orderId") Long orderId) throws Exception {
        List<SellOrderShip> ships = sellOrderService.getOrderShipRecords(orderId);
        return ResponseEntity.ok().body(JSON.toJSONString(ships, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/order/ship/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveShipRecord(@RequestBody SellOrderShip ship,
                                                 @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save ship record.", user.getId());
        SellOrderShip result = sellOrderService.saveOrderShip(user, ship);
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/order/ship/remove/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeShip(@PathVariable Long id,
                                             @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove ship record by id:{}", user.getId(), id);
        sellOrderService.removeOrderShip(user, id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获得销售列表
     * @param allAction
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/all/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> allList(@RequestBody SellOrderAllAction allAction,
                                          @AuthenticationPrincipal User user) throws Exception {
        Integer limit = allAction.getSize() == null ? 50 : allAction.getSize();
        Integer pageNum = allAction.getPage() == null ? 1 : allAction.getPage();
        Integer offset = (pageNum -1) * limit;
        allAction.setCompanyId(user.getCompanyId());
        allAction.setOffset(offset);
        allAction.setLimit(limit);
        List<SellOrder> result = sellOrderService.getAllList(allAction);
        int count = 0;
        if (result.size() > 0) {
            count = sellOrderMapper.getAllCount(allAction);
        }
        JSONObject response = new JSONObject();
        response.put("data", result);
        response.put("count", count);
        return ResponseEntity.ok().body(JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect));
    }

    /**
     * 客户销售情况汇总
     * @param allAction
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/customer/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> customerList(@RequestBody SellOrderAllAction allAction,
                                            @AuthenticationPrincipal User user) throws Exception {
        allAction.setCompanyId(user.getCompanyId());
        List<StatusCount> result = sellOrderService.getStatByCustomer(allAction);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }


    @RequestMapping(value = "/order/invoice/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> invoice(@RequestBody SellOrderInvoice sellOrderInvoice, @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save ship record.", user.getId());
        SellOrder result = sellOrderService.saveOrderInvoice(user, sellOrderInvoice);
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

}
