package com.yiban.erp.controller.buy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.BuyOrderStatus;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.BuyOrderDetailMapper;
import com.yiban.erp.dao.BuyOrderMapper;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.GoodsService;
import com.yiban.erp.service.warehouse.RepertoryInService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buy")
public class BuyOrderController {
    private static final Logger logger = LoggerFactory.getLogger(BuyOrderController.class);

    @Autowired
    private BuyOrderMapper buyOrderMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BuyOrderDetailMapper buyOrderDetailMapper;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user, @RequestBody BuyOrderQuery buyOrderQuery) {
        buyOrderQuery.setCompanyId(user.getCompanyId());
        if (buyOrderQuery.getStartDate() != null) {
            // get start of day
            buyOrderQuery.setStartDate(DateUtils.truncate(buyOrderQuery.getStartDate(), Calendar.DATE));
        }
        if (buyOrderQuery.getEndDate() != null) {
            // get end of day
            buyOrderQuery.setEndDate(DateUtils.truncate(DateUtils.addDays(buyOrderQuery.getEndDate(), 1), Calendar.DATE));
        }
        if (buyOrderQuery.getStatus().equals(BuyOrderStatus.ALL)) {
            buyOrderQuery.setStatus(null);
        }
        List<BuyOrder> buyOrderList = buyOrderMapper.queryOrders(buyOrderQuery);
        return ResponseEntity.ok().body(JSON.toJSONString(buyOrderList));
    }

    @RequestMapping(value = "/orderdetail/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user, @PathVariable Long orderId) {
        List<BuyOrderDetail> buyDetailList = buyOrderDetailMapper.findByOrderId(orderId, user.getCompanyId());
        List<Long> goodsIdList = new ArrayList<>();
        if (buyDetailList != null && !buyDetailList.isEmpty()) {
            //嵌入商品信息
            buyDetailList.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
            List<Goods> goods = goodsService.getGoodsById(goodsIdList);
            Map<Long, Goods> goodsMap = new HashMap<>();
            goods.stream().forEach(item -> goodsMap.put(item.getId(), item)); //按ID转Map
            //嵌入当前库存信息
            Integer warehouseId = buyDetailList.get(0).getWarehouseId(); //逻辑上同一个订单的仓库ID相同
            List<CurrentBalanceResp> balanceResp = repertoryInfoMapper.getBalance(warehouseId, goodsIdList);
            Map<Long, List<CurrentBalanceResp>> balanceMap = balanceResp.stream().collect(Collectors.groupingBy(CurrentBalanceResp::getGoodsId));

            buyDetailList.forEach(item -> {
                item.setGoods(goodsMap.get(item.getGoodsId()));
                CurrentBalanceResp resp = balanceMap.get(item.getGoodsId()) == null ? null : balanceMap.get(item.getGoodsId()).get(0); //如果不为空，逻辑上只有一个
                if (resp != null) {
                    item.setBalance(resp.getBalance());
                }
            });
        }
        return ResponseEntity.ok().body(JSON.toJSONString(buyDetailList));
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateStatus(@AuthenticationPrincipal User user,
                                               @RequestBody BuyOrderQuery query) throws Exception {
        BuyOrder order = buyOrderMapper.selectByPrimaryKey(query.getOrderId());
        if (order == null) {
            throw new BizException(ErrorCode.BUY_ORDER_NOT_EXISTED);
        }
        if (!order.getCompanyId().equals(user.getCompanyId())) {
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        if (!order.canUpdateStatus()) {
            throw new BizException(ErrorCode.BUY_ORDER_IS_CHECKED);
        }
        order.setStatus(query.getOrderStatus());
        order.setCheckBy(user.getNickname());
        order.setCheckResult(query.getCheckResult());
        order.setCheckTime(new Date());
        order.setUpdatedTime(new Date());
        order.setUpdatedBy(user.getNickname());
        int result = buyOrderMapper.updateByPrimaryKeySelective(order);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_UPDATE_FROM_DB.toString());
    }

    @Transactional
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
                                      @RequestBody BuyOrder buyOrder) {
        logger.info("ADD new buy order, userId={}", user.getId());
        if (!validateBuyOrder(buyOrder)) {
            return ResponseEntity.badRequest().body(ErrorCode.BUY_ORDER_PARAMS_INVALID.toString());
        }

        int result = 0;
        if (buyOrder.getId() == null) {
            buyOrder.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.BUY));
            buyOrder.setCompanyId(user.getCompanyId());
            buyOrder.setCreatedBy(user.getNickname());
            buyOrder.setStatus(BuyOrderStatus.INIT);
            buyOrder.setCreatedTime(new Date());
            result = buyOrderMapper.insert(buyOrder);
        } else {
            BuyOrder existingBuyOrder = buyOrderMapper.selectByPrimaryKey(buyOrder.getId());
            if (existingBuyOrder == null) {
                logger.error("Buy order is not existed, query orderId={}", existingBuyOrder.getId());
                return ResponseEntity.badRequest().body(ErrorCode.BUY_ORDER_NOT_EXISTED.toString());
            }
            if (!existingBuyOrder.getCompanyId().equals(user.getCompanyId())) {
                logger.error("Inconsistent company ID, old={} userId={}", existingBuyOrder.getCompanyId(), user.getCompanyId());
                return ResponseEntity.badRequest().body(ErrorCode.ACCESS_PERMISSION.toString());
            }
            if (!existingBuyOrder.canUpdateDetails()) {
                logger.warn("Cannot update order status, already checked, buyOrderId={}", buyOrder.getId());
                return ResponseEntity.badRequest().body(ErrorCode.BUY_ORDER_IS_CHECKED.toString());
            }

            buyOrder.setUpdatedBy(user.getNickname());
            buyOrder.setUpdatedTime(new Date());
            // add company id in query criteria to avoid hack, or we can use order uuid
            buyOrder.setCompanyId(user.getCompanyId());
            result = buyOrderMapper.updateByPrimaryKeySelective(buyOrder);
        }

        if (result > 0) {
            // save order details
            result = saveOrderDetails(buyOrder, user);
            if (result >= 0) {
                JSONObject obj = new JSONObject();
                obj.put("orderId", buyOrder.getId());
                obj.put("status", buyOrder.getStatus());
                return ResponseEntity.ok().body(obj.toString());
            }
        }

        return ResponseEntity.badRequest().body("Failed to insert/update");
    }

    private boolean validateBuyOrder(BuyOrder buyOrder) {
        if (buyOrder == null) {
            return false;
        }
        if (buyOrder.getOrderItems() == null || buyOrder.getOrderItems().isEmpty()) {
            return false;
        }
        if (buyOrder.getSupplierId() == null) {
            return false;
        }
        return true;
    }

    private int saveOrderDetails(BuyOrder buyOrder, User operator) {
        int result = 0;
        // remove existing details
        if (buyOrder != null) {
            if (!buyOrder.canUpdateDetails()) {
                logger.info("Buy order details cannot be edited, orderId={}", buyOrder.getId());
                return -1;
            } else {
                buyOrderDetailMapper.deleteByBuyOrderId(buyOrder.getId());
            }

            // generate from request
            buyOrder.generateOrderDetails();

            if (buyOrder.getDetails() != null) {
                for (BuyOrderDetail detail : buyOrder.getDetails()) {
                    detail.setCreatedBy(operator.getNickname());
                    detail.setCreatedTime(new Date());
                    result += buyOrderDetailMapper.insert(detail);
                }
            }
        }
        return result;
    }
}
