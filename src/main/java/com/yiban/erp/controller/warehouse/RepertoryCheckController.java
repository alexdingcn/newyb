package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.RepertoryCheckPlanDetailMapper;
import com.yiban.erp.dao.RepertoryCheckPlanMapper;
import com.yiban.erp.entities.*;
import com.yiban.erp.service.warehouse.RepertoryCheckPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repertory/check")
public class RepertoryCheckController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryCheckController.class);

    @Autowired
    private RepertoryCheckPlanService repertoryCheckPlanService;
    @Autowired
    private RepertoryCheckPlanMapper repertoryCheckPlanMapper;
    @Autowired
    private RepertoryCheckPlanDetailMapper repertoryCheckPlanDetailMapper;



    //根据条件获取入库单list
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkType", required = false) Integer checkType,
        @RequestParam(name = "counterState", required = false) Integer counterState,
        @RequestParam(name = "warehouseId",required = false) Integer warehouseId,
        @RequestParam(name = "startDate", required = false) Date startDate,
        @RequestParam(name = "endDate", required = false) Date endDate
    ){
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkType", checkType);
        requestMap.put("warehouseId", warehouseId);
        requestMap.put("startDate", startDate);
        requestMap.put("endDate", endDate);
        List<RepertoryCheckPlan> list = repertoryCheckPlanMapper.getCheckPlanList(requestMap);
        JSONObject result = new JSONObject();
        result.put("data", list);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderPartList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkOrderId", required = true) long checkOrderId
    ){
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkOrderId);
        //List<RepertoryCheckPart>  partlist = repertoryCheckPartMapper.selectByCheckOrderId(checkOrderId);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        //result.put("checkPartList", partlist);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfo(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkOrderId", required = true) Long checkOrderId
    ) {
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkOrderId);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderinfoList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfoList(@AuthenticationPrincipal User user,
                                            @RequestParam(name = "checkOrderId", required = true) Long checkOrderId
    ) {

        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkOrderId);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkOrderId", checkOrderId);
        List<RepertoryCheckPlanDetail>  cdlist=repertoryCheckPlanDetailMapper.getCheckPlanDetailList(requestMap);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        result.put("checkDetailList", cdlist);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
        @RequestBody RepertoryCheckPlan repertoryCheckPlan)throws Exception  {
        logger.info("ADD new repertoryCheckPlan order, userId={}", user.getId());
        repertoryCheckPlanService.saveCheckPlan(user,repertoryCheckPlan);
        return ResponseEntity.ok().build();
    }

}
