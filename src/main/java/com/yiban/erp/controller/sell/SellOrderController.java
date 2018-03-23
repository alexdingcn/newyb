package com.yiban.erp.controller.sell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.SellOrderDetail;
import com.yiban.erp.entities.SellOrderShip;
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
import java.util.*;

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
        String reqRefNo = StringUtils.isBlank(refNo) ? null : refNo.trim();
        String reqStatus = StringUtils.isBlank(status) ? null : status;
        Date createOrderDate = StringUtils.isBlank(reqDate) ? null : new Date(Long.valueOf(reqDate));
        List<SellOrder> sellOrders = sellOrderService.getList(user.getCompanyId(), customerId, salerId, reqRefNo, reqStatus, createOrderDate, page, size);
        JSONObject result = new JSONObject();
        if (!sellOrders.isEmpty()) {
            Integer count = sellOrderMapper.getListCount(user.getCompanyId(), customerId, salerId, reqRefNo, reqStatus, createOrderDate);
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

    @RequestMapping(value = "/detail/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> getDetailHistory(HttpServletRequest request,
                                                   @AuthenticationPrincipal User user) throws Exception {
        String customerIdStr = request.getParameter("customerId");
        String goodIdStr = request.getParameter("goodIds");
        Integer customerId = null;
        List<Long> goodIds = new ArrayList<>();
        if (StringUtils.isNotBlank(customerIdStr)) {
            customerId = Integer.parseInt(customerIdStr);
        }
        if (StringUtils.isNotBlank(goodIdStr)) {
            goodIds = JSON.parseArray(goodIdStr, Long.class);
        }
        Map<Long, List<SellOrderDetail>> details = sellOrderService.getDetailHistory(user.getCompanyId(), customerId, goodIds);
        return ResponseEntity.ok().body((JSON) JSON.toJSON(details));
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

    @RequestMapping(value = "/order/ship/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getShipList(@RequestParam("orderId") Long orderId) throws Exception {
        List<SellOrderShip> ships = sellOrderService.getOrderShipRecords(orderId);
        return ResponseEntity.ok().body(JSON.toJSONString(ships));
    }

    @RequestMapping(value = "/order/ship/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveShipRecord(@RequestBody SellOrderShip ship,
                                                 @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save ship record.", user.getId());
        SellOrderShip result = sellOrderService.saveOrderShip(user, ship);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/order/ship/remove/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeShip(@PathVariable Long id,
                                             @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove ship record by id:{}", user.getId(), id);
        sellOrderService.removeOrderShip(user, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/all/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> allList(HttpServletRequest request,
                                          @AuthenticationPrincipal User user) throws Exception {
        String status = request.getParameter("status");
        String customerIdStr = request.getParameter("customerId");
        String orderNumber = request.getParameter("orderNumber");
        String salerIdStr = request.getParameter("salerId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String page = request.getParameter("page");
        String size = request.getParameter("size");

        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("companyId", user.getCompanyId());
        map.put("orderNumber", orderNumber);
        map.put("customerId", StringUtils.isBlank(customerIdStr) ? null : Integer.parseInt(customerIdStr));
        map.put("salerId", StringUtils.isBlank(salerIdStr) ? null : Integer.parseInt(salerIdStr));
        map.put("startDate", StringUtils.isBlank(startDateStr) ? null : new Date(Long.valueOf(startDateStr)));
        map.put("endDate", StringUtils.isBlank(endDateStr) ? null : new Date(Long.valueOf(endDateStr)));
        Integer limit = StringUtils.isBlank(size) ? null : Integer.parseInt(size);
        Integer pageNum = StringUtils.isBlank(page) || limit == null ? null : Integer.parseInt(page);
        Integer offset = pageNum != null && limit != null ? ((pageNum -1) * limit) : null;
        map.put("offset", offset);
        map.put("limit", limit);
        List<SellOrder> result = sellOrderMapper.getAllList(map);
        int count = 0;
        if (result.size() > 0) {
            count = sellOrderMapper.getAllCount(map);
        }
        JSONObject response = new JSONObject();
        response.put("data", result);
        response.put("count", count);
        return ResponseEntity.ok().body(response.toJSONString());
    }

}
