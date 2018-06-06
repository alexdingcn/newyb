package com.yiban.erp.controller.sell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yiban.erp.constant.FinancialBizType;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.dao.SellOrderPaymentMapper;
import com.yiban.erp.dto.FinancialReq;
import com.yiban.erp.dto.SellOrderQuery;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.SellOrderPayment;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.financial.FinancialService;
import com.yiban.erp.service.sell.SellOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/sell/payment")
public class SellOrderPaymentController {
    private static final Logger logger = LoggerFactory.getLogger(SellOrderPaymentController.class);
    @Autowired
    private SellOrderMapper sellOrderMapper;
    @Autowired
    private SellOrderService sellOrderService;
    @Autowired
    private SellOrderPaymentMapper sellOrderPaymentMapper;

    @Autowired
    private FinancialService financialService;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrderList(@RequestBody SellOrderQuery query,
                                               @AuthenticationPrincipal User user) throws Exception {
        query.setCompanyId(user.getCompanyId());
        Integer count = sellOrderService.getOrderPaymentHistoryCount(query);
        JSONObject result = new JSONObject();
        if (count != null && count > 0) {
            List<SellOrderPayment> payments = sellOrderService.getOrderPaymentHistory(query);
            result.put("count", count);
            result.put("data", payments);
        } else {
            result.put("count", 0);
            result.put("data", new ArrayList<SellOrderPayment>());
        }
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPayment(@RequestBody SellOrderPayment payment,
                                             @AuthenticationPrincipal User user) throws Exception {
        payment.setCompanyId(user.getCompanyId());
        if (payment.getOrderId() == null || BigDecimal.ZERO.equals(payment.getPayAmount())) {
            return ResponseEntity.badRequest().body(ErrorCode.PARAMETER_MISSING.toString());
        }
        SellOrder sellOrder = sellOrderMapper.selectOrderDetailById(payment.getOrderId());
        if (sellOrder == null) {
            return ResponseEntity.badRequest().body(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL.toString());
        }

        boolean result = sellOrderService.addPayment(payment.getOrderId(), payment, user);
        if (result) {
            int insertResult = sellOrderPaymentMapper.insertSelective(payment);
            if (insertResult > 0) {
                // 添加财务流水
                FinancialReq financialReq = new FinancialReq();
                financialReq.setBizRefId(payment.getOrderId());
                financialReq.setBizRefNo(sellOrder.getOrderNumber());
                financialReq.setBizType(FinancialBizType.RECEIVE.name());
                financialReq.setCustId(sellOrder.getCustomerId());
                financialReq.setCustType(FinancialReq.CUST_CUSTOMER);
                financialReq.setCompanyId(user.getCompanyId());
                financialReq.setLogUserName(user.getNickname());
                financialReq.setLogAmount(payment.getPayAmount());
                financialReq.setLogAccount(sellOrder.getCustomerName());
                financialReq.setLogDate(new Date());
                logger.info("user:{} add financial flow by:{}", user.getId(), JSON.toJSONString(financialReq));
                financialService.payAndReceiveFinancialRecord(financialReq);

                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body(ErrorCode.FAILED_INSERT_FROM_DB.toString());
        }
        return ResponseEntity.badRequest().body(ErrorCode.SELL_ORDER_UPDATE_PAYMENT_FAIL.toString());
    }
}
