package com.yiban.erp.controller.financial;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.FinancialFlowMapper;
import com.yiban.erp.dto.FinancialQuery;
import com.yiban.erp.dto.FinancialReq;
import com.yiban.erp.entities.FinancialFlow;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.financial.FinancialService;
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

import java.util.List;

@RestController
@RequestMapping("/financial")
public class FinancialController {

    private static final Logger logger = LoggerFactory.getLogger(FinancialController.class);

    @Autowired
    private FinancialService financialService;
    @Autowired
    private FinancialFlowMapper financialFlowMapper;

    @RequestMapping(value = "/flow/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> flowList(@RequestBody FinancialQuery query,
                                           @AuthenticationPrincipal User user) throws Exception {
        query.setCompanyId(user.getCompanyId());
        List<FinancialFlow> flows = financialFlowMapper.getFlowList(query);
        Integer count = financialFlowMapper.getFlowListCount(query);
        JSONObject response = new JSONObject();
        response.put("data", flows);
        response.put("count", count);
        return ResponseEntity.ok().body(response.toJSONString());
    }

    @RequestMapping(value = "/flow/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> flowAdd(@RequestBody FinancialReq financialReq,
                                          @AuthenticationPrincipal User user) throws Exception {
        financialReq.setCompanyId(user.getCompanyId());
        financialReq.setLogUserName(user.getNickname());
        logger.info("user:{} add financial flow by:{}", user.getId(), JSON.toJSONString(financialReq));
        financialService.payAndReceiveFinancialRecord(financialReq);
        logger.info("user:{} add financial flow success.", user.getId());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/flow/cancel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> flowCancel(@RequestBody FinancialFlow flow,
                                             @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} do request cancel financial flow:{}", user.getId(), JSON.toJSONString(flow));
        financialService.flowCancel(flow, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/flow/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> flowUpdate(@RequestBody FinancialFlow flow,
                                             @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} update flow:{}", user.getId(), JSON.toJSONString(flow));
        FinancialFlow newFlow = financialService.flowUpdate(flow, user);
        return ResponseEntity.ok().body(JSON.toJSONString(newFlow));
    }

}
