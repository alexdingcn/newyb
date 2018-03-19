package com.yiban.erp.controller.sell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.SellOrderDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.sell.SellOrderService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<String> getOrderList(HttpServletRequest request,
                                               @AuthenticationPrincipal User user) throws Exception {

        String reqCustomerId = request.getParameter("customerId");
        String reqSalerId = request.getParameter("salerId");
        String refNo = request.getParameter("refNo");
        String status = request.getParameter("status");
        String reqDate = request.getParameter("createOrderDate");
        String reqPage = request.getParameter("page");
        String reqSize = request.getParameter("size");
        Integer customerId = StringUtils.isBlank(reqCustomerId) ? null : Integer.parseInt(reqCustomerId);
        Integer salerId = StringUtils.isBlank(reqSalerId) ? null : Integer.parseInt(reqSalerId);
        Integer page = StringUtils.isBlank(reqPage) ? 1 : Integer.parseInt(reqPage);
        Integer size = StringUtils.isBlank(reqSize) ? 10 : Integer.parseInt(reqSize);
        Date createOrderDate = StringUtils.isBlank(reqDate) ? null : new Date(Long.valueOf(reqDate));
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

    @RequestMapping(value = "/detail/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailList(@RequestParam("sellOrderId") Long sellOrderId) throws Exception {
        List<SellOrderDetail> details = sellOrderService.getDetailList(sellOrderId);
        return ResponseEntity.ok().body(JSON.toJSONString(details));
    }

    @RequestMapping(value = "/detail/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailSave(@RequestBody List<SellOrderDetail> details,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save sell order detail:{}", user.getId(), JSON.toJSONString(details));
        if (details == null || details.isEmpty()) {
            logger.warn("request save sell order detail but params is empty.");
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_EMPTY);
        }
        int count = sellOrderService.detailSave(user, details);
        Long sellOrderId = details.get(0) == null ? null : details.get(0).getSellOrderId();
        List<SellOrderDetail> saveAfterList = sellOrderService.getDetailList(sellOrderId);
        JSONObject result = new JSONObject();
        result.put("success", count); //保存成功的笔数
        result.put("fail", (details.size() - count)); //保存失败的笔数
        result.put("detailList", saveAfterList);
        return ResponseEntity.ok().body(result.toJSONString());
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

    @RequestMapping(value = "/order/review/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reviewList(HttpServletRequest request,
                                             @AuthenticationPrincipal User user) throws Exception {
        String reviewType = request.getParameter("reviewType");
        String orderNumber = request.getParameter("orderNumber");
        String salerIdStr = request.getParameter("salerId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        logger.debug("user:{} get review list by params:{}, {}, {}, {}, {}", user.getId(),
                reviewType, orderNumber, salerIdStr, startDateStr, endDateStr);
        Integer salerId = StringUtils.isBlank(salerIdStr) ? null : Integer.parseInt(salerIdStr);
        Date startDate = StringUtils.isBlank(startDateStr) ? null : new Date(Long.valueOf(startDateStr));
        Date endDate = StringUtils.isBlank(endDateStr) ? null : new Date(Long.valueOf(endDateStr));
        List<SellOrder> result = sellOrderService.getReviewList(user.getCompanyId(), reviewType, orderNumber, salerId,
                startDate, endDate);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/order/review/submit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitReview(@RequestBody String reqJson,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} submit sell order review, params:{}", user.getId(), reqJson);
        JSONObject params = JSON.parseObject(reqJson);
        String reviewType = params.getString("reviewType");
        String idStr = params.getString("orderIdList");
        List<Long> idList = JSON.parseArray(idStr, Long.class);
        sellOrderService.submitOrderReview(user, reviewType, idList);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/review/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reviewDetail(@RequestParam("orderId") Long orderId) throws Exception {
        SellOrder order = sellOrderService.reviewDetai(orderId);
        if (order == null) {
            logger.warn("get order detail fail by id:{}", orderId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }else {
            return ResponseEntity.ok().body(JSON.toJSONString(order));
        }
    }

}
