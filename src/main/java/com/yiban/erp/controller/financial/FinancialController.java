package com.yiban.erp.controller.financial;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yiban.erp.constant.FinancialBizType;
import com.yiban.erp.dao.FinancialFlowMapper;
import com.yiban.erp.dao.FinancialPrePaidMapper;
import com.yiban.erp.dao.FinancialPreReceiveMapper;
import com.yiban.erp.dto.FinancialDetailResult;
import com.yiban.erp.dto.FinancialOffsetReq;
import com.yiban.erp.dto.FinancialQuery;
import com.yiban.erp.dto.FinancialReq;
import com.yiban.erp.entities.FinancialFlow;
import com.yiban.erp.entities.FinancialPreRecord;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.financial.FinancialService;
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
@RequestMapping("/financial")
public class FinancialController {

    private static final Logger logger = LoggerFactory.getLogger(FinancialController.class);

    @Autowired
    private FinancialService financialService;
    @Autowired
    private FinancialFlowMapper financialFlowMapper;
    @Autowired
    private FinancialPrePaidMapper financialPrePaidMapper;
    @Autowired
    private FinancialPreReceiveMapper financialPreReceiveMapper;

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

    @RequestMapping(value = "/pre/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPrePaidList(@RequestBody FinancialQuery query,
                                                 @AuthenticationPrincipal User user) throws Exception {
        query.setCompanyId(user.getCompanyId());
        Integer count = 0;
        List<FinancialPreRecord> data = new ArrayList<>();
        if (FinancialBizType.PRE_RECEIVE.name().equalsIgnoreCase(query.getPreBizType())) {
            count = financialPreReceiveMapper.getSearchListCount(query);
            if (count > 0) {
                data = financialPreReceiveMapper.getSearchList(query);
            }
        }else if (FinancialBizType.PRE_PAID.name().equalsIgnoreCase(query.getPreBizType())) {
            count = financialPrePaidMapper.getSearchListCount(query);
            if (count > 0) {
                data = financialPrePaidMapper.getSearchList(query);
            }
        }
        JSONObject response = new JSONObject();
        response.put("data", data);
        response.put("count", count);
        return ResponseEntity.ok().body(response.toJSONString());
    }

    @RequestMapping(value = "/pre/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPreRecord(@RequestBody FinancialPreRecord preRecord,
                                             @AuthenticationPrincipal User user) throws Exception {
        preRecord.setCompanyId(user.getCompanyId());
        preRecord.setLogUserId(user.getId());
        preRecord.setCreatedBy(user.getNickname());
        logger.info("user:{} request add pre record by: {}", user.getId(), JSON.toJSONString(preRecord));
        financialService.addPreRecord(preRecord);
        logger.info("user:{} add pre record success.", user.getId());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/pre/cancel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> preCancel(@RequestBody FinancialPreRecord preRecord,
                                                @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request cancel pre record. by:{}", user.getId(), JSON.toJSONString(preRecord));
        financialService.preCancel(preRecord, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/pre/offset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> preOffset(@RequestBody FinancialOffsetReq offsetReq,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} request offset pre record by:{}", user.getId(), JSON.toJSONString(offsetReq));
        financialService.preOffset(offsetReq, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/flow/{flowId}/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> flowDetailInfo(@PathVariable Long flowId,
                                             @AuthenticationPrincipal User user) throws Exception {

        logger.debug("get financial flow detail info by flowId:{} user:{}", flowId, user.getId());
        FinancialDetailResult result = financialService.flowDetailInfo(flowId, user);
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/flow/{flowId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFlowById(@PathVariable Long flowId, @AuthenticationPrincipal User user) {
        FinancialFlow flow = financialFlowMapper.getIncludeOptionById(flowId);
        return ResponseEntity.ok().body(JSON.toJSONString(flow));
    }

}
