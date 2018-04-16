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
import org.springframework.web.bind.annotation.*;

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
        @RequestParam(name = "checkPlanId", required = true) long checkPlanId
    ){
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkPlanId);
        //List<RepertoryCheckPart>  partlist = repertoryCheckPartMapper.selectBycheckPlanId(checkPlanId);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        //result.put("checkPartList", partlist);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfo(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkPlanId", required = true) Long checkPlanId
    ) {
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkPlanId);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //查询盘点单全部信息
    @RequestMapping(value = "/orderinfoList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfoList(@AuthenticationPrincipal User user,
                @RequestParam(name = "checkPlanId", required = true) Long checkPlanId
    ) throws Exception {
        JSONObject result = new JSONObject();
        result =repertoryCheckPlanService.getCheckPlanAndDetailJSON(checkPlanId);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //盘点单审核通过
    @RequestMapping(value = "/checkPlanPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkPlanPass(@AuthenticationPrincipal User user,
                                                @RequestBody RepertoryCheckPlan rp)throws Exception {
        JSONObject result = new JSONObject();
        result =repertoryCheckPlanService.getCheckPlanPassJSON(user,rp);
        return ResponseEntity.ok().body(result.toJSONString());
    }


    //根据盘点计划和拼命查询选择数据
    @RequestMapping(value = "/orderinfoList4Search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfoList4Search(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkPlanId", required = true) Long checkPlanId,
        @RequestParam(name = "goodSearch", required = false) String goodSearch
    ) throws Exception {
        JSONObject result = new JSONObject();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkPlanId", checkPlanId);
        requestMap.put("goodSearch", goodSearch);
        result = repertoryCheckPlanService.getCheckPlanDetail4SearchJSON(requestMap);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //查询某人执行的盘点单进度
    @RequestMapping(value = "/orderinfoListByUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfoListByUser(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkPlanId", required = true) Long checkPlanId
    ) throws Exception {
        JSONObject result = new JSONObject();
        result =repertoryCheckPlanService.getCheckPlanAndDetailJSONByUser(user,checkPlanId);
        return ResponseEntity.ok().body(result.toJSONString());
    }




    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
        @RequestBody RepertoryCheckPlan repertoryCheckPlan)throws Exception  {
        logger.info("ADD new repertoryCheckPlan order, userId={}", user.getId());
        repertoryCheckPlanService.saveCheckPlan(user,repertoryCheckPlan);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/doCheckDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> doCheckDetail(@AuthenticationPrincipal User user,
        @RequestBody RepertoryCheckPlanDetail repertoryCheckPlanDetail)throws Exception  {
        JSONObject result = new JSONObject();
        result=repertoryCheckPlanService.doCheckDetail(user,repertoryCheckPlanDetail);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //盘盈数据-可能包含本次盘点范围外的产品
    @RequestMapping(value = "/doCheckDetailMore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> doCheckDetailMore(@AuthenticationPrincipal User user,
                                                @RequestBody RepertoryCheckPlanDetail repertoryCheckPlanDetail)throws Exception  {
        JSONObject result = new JSONObject();
        result=repertoryCheckPlanService.doCheckDetailMore(user,repertoryCheckPlanDetail);
        return ResponseEntity.ok().body(result.toJSONString());
    }
    //增加盘盈记录
    @RequestMapping(value = "/addPlanDetaile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPlanDetaile(@AuthenticationPrincipal User user,
                                      @RequestBody RepertoryCheckPlanDetail repertoryCheckPlanDetail)throws Exception  {
        logger.info("ADD new repertoryCheckPlan order, userId={}", user.getId());

        repertoryCheckPlanService.saveCheckPlanDetail(user,repertoryCheckPlanDetail);
        return ResponseEntity.ok().build();
    }



}
