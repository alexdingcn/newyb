package com.yiban.erp.controller.buy;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.entities.RepositoryOrder;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.buy.ReceiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receive")
public class BuyReceiveController {

    private static final Logger logger = LoggerFactory.getLogger(BuyReceiveController.class);

    @Autowired
    private ReceiveService receiveService;

    /**
     * 获取某一产品当前采购的订单数，当前库存，和最近一次的采购价
     * @param warehouseId
     * @param goodsId
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/current/balance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> currentBalance(@RequestParam(name = "warehouseId") Integer warehouseId,
                                                 @RequestParam(name = "goodsId") Long goodsId,
                                                 @AuthenticationPrincipal User user) throws Exception {
        CurrentBalanceResp resp = receiveService.getCurrentBalance(warehouseId, goodsId);
        return ResponseEntity.ok().body(JSON.toJSONString(resp));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveOrder(@RequestBody RepositoryOrder order,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save repository order info. orderId:{}", user.getId(), order.getId());
        receiveService.saveOrder(user, order);
        return ResponseEntity.ok().build();
    }

}
