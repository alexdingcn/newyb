package com.yiban.erp.controller.buy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.entities.RepositoryOrder;
import com.yiban.erp.entities.RepositoryOrderDetail;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.buy.ReceiveService;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/receive")
public class BuyReceiveController {

    private static final Logger logger = LoggerFactory.getLogger(BuyReceiveController.class);

    @Autowired
    private ReceiveService receiveService;

    /**
     * 获取某一产品当前采购的订单数，当前库存，和最近一次的采购价
     * @param warehouseId
     * @param goodsIdListStr
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/current/balance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> currentBalance(@RequestParam(name = "warehouseId") Integer warehouseId,
                                                 @RequestParam(name = "goodsIdList") String goodsIdListStr,
                                                 @AuthenticationPrincipal User user) throws Exception {
        if (StringUtils.isBlank(goodsIdListStr)) {
            return ResponseEntity.ok().build();
        }
        List<Long> goodsIdList = JSON.parseArray(goodsIdListStr, Long.class);
        Map<Long, CurrentBalanceResp> resp = receiveService.getCurrentBalance(warehouseId, goodsIdList);
        return ResponseEntity.ok().body((JSON) JSON.toJSON(resp));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveOrder(@RequestBody RepositoryOrder order,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save repository order info. orderId:{}", user.getId(), order.getId());
        receiveService.saveOrder(user, order);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestBody ReceiveListReq listReq,
                                       @AuthenticationPrincipal User user) throws Exception {
        if (listReq == null || listReq.getStatusList() == null || listReq.getStatusList().isEmpty()) {
            throw new BizException(ErrorCode.RECEIVE_QUERY_PARAM_ERROR);
        }
        listReq.setCompanyId(user.getCompanyId());
        List<RepositoryOrder> result = receiveService.getList(listReq);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeOrder(@PathVariable Long id,
                                              @AuthenticationPrincipal User user) throws Exception {
        logger.info("user: {} remove order id:{}", user.getId(), id);
        receiveService.removeById(user, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/buy/order/{buyOrderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getByBuyOrder(@PathVariable Long buyOrderId,
                                                @AuthenticationPrincipal User user) throws Exception {
        RepositoryOrder order = receiveService.getByBuyOrder(user, buyOrderId);
        return ResponseEntity.ok().body(JSON.toJSONString(order));
    }

    @RequestMapping(value = "/set/checkTemp", method = RequestMethod.PUT, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setOrderCheckTemp(@RequestBody ReceiveSetReq setReq,
                                                    @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set order:{} check temp to :{}", user.getId(), setReq.getOrderId(), setReq.getCheckTemp());
        receiveService.setOrderCheckTemp(setReq, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/survey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setDetailSurvey(@RequestBody ReceiveSetReq setReq,
                                                  @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set order detail survey params:{}", user.getId(), JSON.toJSONString(setReq));
        receiveService.setDetailSurvey(user, setReq);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrderDetails(@PathVariable Long orderId) {
        List<RepositoryOrderDetail> details = receiveService.getDetailList(orderId);
        return ResponseEntity.ok().body(JSON.toJSONString(details));
    }

    @RequestMapping(value = "/detail/remove/{detailId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailRemove(@PathVariable Long detailId,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} remove order detail by id:{}", user.getId(), detailId);
        receiveService.removeDetail(user, detailId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/check", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setCheck(@RequestBody ReceiveSetReq setReq,
                                           @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} submit check info {}", user.getId(), JSON.toJSONString(setReq));
        receiveService.setCheckResult(user, setReq);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/check/order/{orderId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setUnCheckByOrder(@PathVariable Long orderId,
                                                    @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set uncheck order:{}", user.getId(), orderId);
        receiveService.setUncheckOrder(user, orderId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/check/detail/{detailId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setUnCheckByDetail(@PathVariable Long detailId,
                                                    @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set uncheck order detail:{}", user.getId(), detailId);
        receiveService.setUncheckDetail(user, detailId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/save/detail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setSaveDetail(@RequestBody ReceiveSetReq setReq,
                                                @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} update order detail info.", user.getId());
        int count = receiveService.setSaveDetail(user, setReq);
        JSONObject response = new JSONObject();
        response.put("count", count);
        return ResponseEntity.ok().body(response.toJSONString());
    }

    @RequestMapping(value = "/order/fileNo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setOrderFileNo(@RequestBody ReceiveSetReq setReq,
                                                 @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set order:{} fileNo:{}", user.getId(), setReq.getOrderId(), setReq.getFileNo());
        receiveService.setOrderFileNo(user, setReq.getOrderId(), setReq.getFileNo());
        return ResponseEntity.ok().build();
    }

}
