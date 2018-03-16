package com.yiban.erp.controller.buy;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.BuyOrderStatus;
import com.yiban.erp.dao.BuyOrderDetailMapper;
import com.yiban.erp.dao.BuyOrderMapper;
import com.yiban.erp.entities.BuyOrder;
import com.yiban.erp.entities.BuyOrderDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/buy")
public class BuyOrderController {
    private static final Logger logger = LoggerFactory.getLogger(BuyOrderController.class);

    @Autowired
    private BuyOrderMapper buyOrderMapper;

    @Autowired
    private BuyOrderDetailMapper buyOrderDetailMapper;

    @Transactional
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
                                      @RequestBody BuyOrder buyOrder) {
        logger.info("ADD new buy order, userId={}", user.getId());

        // TODO: add validate
        if (!validateBuyOrder(buyOrder)) {
            return ResponseEntity.badRequest().body(ErrorCode.BUY_ORDER_PARAMS_INVALID.toString());
        }

        int result = 0;
        if (buyOrder.getId() == null) {
            buyOrder.setOrderNumber(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + RandomStringUtils.randomNumeric(5));
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
