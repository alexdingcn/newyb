package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.entities.RepertoryInDetail;
import com.yiban.erp.entities.RepertoryOut;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.warehouse.RepertoryInService;
import com.yiban.erp.service.warehouse.RepertoryOutService;
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
@RequestMapping("/repertory/out")
public class RepertoryOutController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryOutController.class);

    @Autowired
    private RepertoryOutService repertoryOutService;



    /**
     *移库出库登记信息
     * @param repertoryOut
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/changeRepertoryOut", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeRepertoryOut(@RequestBody RepertoryOut repertoryOut, @AuthenticationPrincipal User user) throws Exception {
        JSONObject response = new JSONObject();

        repertoryOutService.saveChangeRepertoryOut(user,repertoryOut);

        return ResponseEntity.ok().body(response.toJSONString());
    }

}
