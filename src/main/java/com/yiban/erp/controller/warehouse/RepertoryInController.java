package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yiban.erp.dao.RepertoryInMapper;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.dto.RepertoryInListReq;
import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.entities.RepertoryInDetail;
import com.yiban.erp.entities.RepertoryOutDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.warehouse.RepertoryInService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repertory/in")
public class RepertoryInController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryInController.class);

    @Autowired
    private RepertoryInService repertoryInService;
    @Autowired
    private RepertoryInMapper repertoryInMapper;

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
        Map<Long, CurrentBalanceResp> resp = repertoryInService.getCurrentBalance(warehouseId, goodsIdList);
        return ResponseEntity.ok().body((JSON) JSON.toJSON(resp));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveOrder(@RequestBody RepertoryIn order,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save repository order info. orderId:{}", user.getId(), order.getId());
        repertoryInService.saveOrder(user, order);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestBody ReceiveListReq listReq,
                                       @AuthenticationPrincipal User user) throws Exception {
        if (listReq == null || listReq.getStatusList() == null || listReq.getStatusList().isEmpty()) {
            throw new BizException(ErrorCode.RECEIVE_QUERY_PARAM_ERROR);
        }
        if (listReq.getEndReceiveDate() != null) {
            listReq.setEndReceiveDate(DateUtils.truncate(DateUtils.addDays(listReq.getEndReceiveDate(), 1), Calendar.DATE));
        }
        listReq.setCompanyId(user.getCompanyId());
        List<RepertoryIn> result = repertoryInService.getList(listReq);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeOrder(@PathVariable Long id,
                                              @AuthenticationPrincipal User user) throws Exception {
        logger.info("user: {} remove order id:{}", user.getId(), id);
        repertoryInService.removeById(user, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/buy/order/{buyOrderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getByBuyOrder(@PathVariable Long buyOrderId,
                                                @AuthenticationPrincipal User user) throws Exception {
        RepertoryIn order = repertoryInService.getByBuyOrder(user, buyOrderId);
        return ResponseEntity.ok().body(JSON.toJSONString(order));
    }

    @RequestMapping(value = "/set/checkTemp", method = RequestMethod.PUT, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setOrderCheckTemp(@RequestBody ReceiveSetReq setReq,
                                                    @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set order:{} check temp to :{}", user.getId(), setReq.getOrderId(), setReq.getCheckTemp());
        repertoryInService.setOrderCheckTemp(setReq, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/survey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setDetailSurvey(@RequestBody ReceiveSetReq setReq,
                                                  @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set order detail survey params:{}", user.getId(), JSON.toJSONString(setReq));
        repertoryInService.setDetailSurvey(user, setReq);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrderDetails(@PathVariable Long orderId) throws Exception {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(orderId);
        if (order == null || order.getId() == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        List<RepertoryInDetail> details = repertoryInService.getDetailList(order);
        return ResponseEntity.ok().body(JSON.toJSONString(details));
    }

    @RequestMapping(value = "/detail/remove/{detailId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailRemove(@PathVariable Long detailId,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} remove order detail by id:{}", user.getId(), detailId);
        repertoryInService.removeDetail(user, detailId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/check", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setCheck(@RequestBody ReceiveSetReq setReq,
                                           @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} submit check info {}", user.getId(), JSON.toJSONString(setReq));
        int refresh = repertoryInService.setCheckResult(user, setReq);
        JSONObject result = new JSONObject();
        result.put("refresh", refresh);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/set/check/order/{orderId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setUnCheckByOrder(@PathVariable Long orderId,
                                                    @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set uncheck order:{}", user.getId(), orderId);
        repertoryInService.setUncheckOrder(user, orderId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/check/detail/{detailId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setUnCheckByDetail(@PathVariable Long detailId,
                                                    @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set uncheck order detail:{}", user.getId(), detailId);
        repertoryInService.setUncheckDetail(user, detailId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/fileNo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setOrderFileNo(@RequestBody ReceiveSetReq setReq,
                                                 @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} set order:{} fileNo:{}", user.getId(), setReq.getOrderId(), setReq.getFileNo());
        repertoryInService.setOrderFileNo(user, setReq.getOrderId(), setReq.getFileNo());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/set/incheck", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setOrderInCheck(@RequestBody ReceiveSetReq setReq,
                                                  @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request order in repository check for orderId:{}", user.getId(), setReq.getOrderId());
        repertoryInService.setOrderInCheck(user, setReq.getOrderId());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/detaillist", method = RequestMethod.POST, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detaillist(@RequestBody RepertoryInListReq listReq,
                                             @AuthenticationPrincipal User user) throws Exception {
        Date endDate=listReq.getEndInDate();
        if(endDate!=null){
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            end.set(Calendar.HOUR_OF_DAY,23);
            end.set( Calendar.MINUTE,59);
            end.set(Calendar.SECOND,59);
            listReq.setEndInDate(end.getTime());
        }
        listReq.setCompanyId(user.getCompanyId());
        listReq.setInStatus("IN_CHECKED");
        List<RepertoryInDetail> result = repertoryInService.getInDetailList(listReq);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/order/view/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderView(@PathVariable Long orderId,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.debug("user:{} get repertory in order view by id:{}", user.getId(), orderId);
        RepertoryIn result = repertoryInService.getOrderView(orderId, user);
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

}
