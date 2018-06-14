package com.yiban.erp.controller.buy;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yiban.erp.dto.BuyBackReq;
import com.yiban.erp.entities.RepertoryInBack;
import com.yiban.erp.entities.RepertoryInBackDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.buy.BuyBackService;
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
@RequestMapping("/buy/back")
public class BuyBackController {

    private static final Logger logger = LoggerFactory.getLogger(BuyBackController.class);

    @Autowired
    private BuyBackService buyBackService;


    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody RepertoryInBack inBack,
                                      @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request add buy back apply, request params:{}", user.getId(), JSON.toJSONString(inBack));
        buyBackService.save(inBack, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestBody BuyBackReq buyBackReq,
                                       @AuthenticationPrincipal User user) throws Exception {
        buyBackReq.setCompanyId(user.getCompanyId());
        List<RepertoryInBack> backs = buyBackService.getList(buyBackReq);
        return ResponseEntity.ok().body(JSON.toJSONString(backs, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/{backId}/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> details(@PathVariable Long backId) {
        if (backId == null) {
            return ResponseEntity.ok().body(JSON.toJSONString(new ArrayList<>()));
        }
        List<RepertoryInBackDetail> details = buyBackService.getDetailByBackId(backId);
        return ResponseEntity.ok().body(JSON.toJSONString(details, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> backOrderCheck(@RequestBody BuyBackReq buyBackReq,
                                                 @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} check back order:{}", user.getId(), JSON.toJSONString(buyBackReq));
        buyBackService.check(buyBackReq, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/check/cancel/{backId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkCancel(@PathVariable Long backId,
                                              @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request check cancel backId:{}", user.getId(), backId);
        buyBackService.checkCancel(backId, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{backId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeBackOrder(@PathVariable Long backId,
                                                  @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} request remove in back order id:{}", user.getId(), backId);
        buyBackService.removeBackOrder(backId, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> viewOrder(@PathVariable Long id,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.debug("user:{} get buy back order view by id:{}", user.getId(), id);
        RepertoryInBack inBack = buyBackService.getViewOrder(id, user);
        return ResponseEntity.ok().body(JSON.toJSONString(inBack, SerializerFeature.DisableCircularReferenceDetect));
    }


}
