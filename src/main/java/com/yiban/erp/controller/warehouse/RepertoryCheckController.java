package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.CheckPlanConstant;
import com.yiban.erp.dao.RepertoryCheckFormMapper;
import com.yiban.erp.dao.RepertoryCheckPlanDetailMapper;
import com.yiban.erp.dao.RepertoryCheckPlanMapper;
import com.yiban.erp.dto.CheckPlanFormListReq;
import com.yiban.erp.dto.CheckPlanListReq;
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
    @Autowired
    private RepertoryCheckFormMapper repertoryCheckFormMapper;
    //根据条件获取入库单list
    @RequestMapping(value = "/list", method = RequestMethod.POST, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestBody CheckPlanListReq listReq,
    @AuthenticationPrincipal User user) throws Exception{

        List<RepertoryCheckPlan> list = repertoryCheckPlanMapper.getCheckPlanList(listReq);
        JSONObject result = new JSONObject();
        result.put("data", list);
        return ResponseEntity.ok().body(result.toJSONString());
    }



    //查询待执行的盘点计划
    @RequestMapping(value = "/doList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> doList(@AuthenticationPrincipal User user){
        CheckPlanListReq listReq=new CheckPlanListReq();
        listReq.setCompanyId(user.getCompanyId());
        listReq.setState(CheckPlanConstant.PLAN_UNCHECK);
        List<RepertoryCheckPlan> list = repertoryCheckPlanMapper.getCheckPlanList(listReq);
        JSONObject result = new JSONObject();
        result.put("data", list);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderPartList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderPartList(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkPlanId", required = true) long checkPlanId
    ){
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkPlanId);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkPlanId", checkPlanId);
        List<RepertoryCheckForm>  partlist = repertoryCheckFormMapper.getCheckPlanFormList(requestMap);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        result.put("checkPartList", partlist);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/orderPartDoList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderPartDoList(@RequestBody CheckPlanFormListReq listReq, @AuthenticationPrincipal User user
        ){
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("formState",listReq.getFormState());
        requestMap.put("startDate",listReq.getStartDate());
        requestMap.put("endDate",listReq.getEndDate());
        List<RepertoryCheckForm>  partlist = repertoryCheckFormMapper.getCheckPlanFormList(requestMap);
        JSONObject result = new JSONObject();
        result.put("checkPartList", partlist);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //查询盘点单全部信息
    @RequestMapping(value = "/getCheckFormDetailList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCheckFormDetailList(@AuthenticationPrincipal User user,
                                                @RequestParam(name = "formId", required = true) Long formId
    ) throws Exception {
        JSONObject result = new JSONObject();
        result =repertoryCheckPlanService.getCheckPlanAndDetailJSONByFormId(user,formId);
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


    //根据盘点计划和拼命查询选择数据
    @RequestMapping(value = "/orderinfoList4Search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfoList4Search(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkPlanId", required = true) Long checkPlanId,
        @RequestParam(name = "goodSearch", required = false) String goodSearch
    ) throws Exception {
        JSONObject result = new JSONObject();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkPlanId", checkPlanId);
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

    //查询盘点单进度
    @RequestMapping(value = "/orderinfoListFormId", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfoListFormId(@AuthenticationPrincipal User user,
        @RequestParam(name = "formId", required = true) Long formId
    ) throws Exception {
        JSONObject result = new JSONObject();
        result =repertoryCheckPlanService.getCheckPlanAndDetailJSONByFormId(user,formId);
        return ResponseEntity.ok().body(result.toJSONString());
    }



    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
        @RequestBody RepertoryCheckPlan repertoryCheckPlan)throws Exception  {
        logger.info("ADD new repertoryCheckPlan order, userId={}", user.getId());
        JSONObject result = new JSONObject();
        if(null !=repertoryCheckPlan.getId() && repertoryCheckPlan.getId()>0){
            result=repertoryCheckPlanService.updateCheckPlan(user,repertoryCheckPlan);
        }else{
            result=repertoryCheckPlanService.saveCheckPlan(user,repertoryCheckPlan);
        }

        return ResponseEntity.ok().body(result.toJSONString());
    }




    //更新
    @RequestMapping(value = "/addOrUpdateForm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addOrUpdateForm(@AuthenticationPrincipal User user,
                                      @RequestBody RepertoryCheckForm repertoryCheckForm)throws Exception  {
        logger.info("ADD new repertoryCheckPlan order, userId={}", user.getId());
        JSONObject result = new JSONObject();
        if(null !=repertoryCheckForm.getId() && repertoryCheckForm.getId()>0){
            RepertoryCheckForm   newForm= repertoryCheckFormMapper.selectByPrimaryKey(repertoryCheckForm.getId());
            newForm.setCheckNote(repertoryCheckForm.getCheckNote());
            repertoryCheckFormMapper.updateByPrimaryKeySelective(newForm);
        }else{
            repertoryCheckFormMapper.insert(repertoryCheckForm);
        }
        result.put("RepertoryCheckForm",  repertoryCheckForm);
        return ResponseEntity.ok().body(result.toJSONString());
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
    //查询盘点单全部信息
    @RequestMapping(value = "/getInfo4Pass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getInfo4Pass(@AuthenticationPrincipal User user,
        @RequestParam(name = "checkPlanId", required = true) Long checkPlanId
    ) throws Exception {
        JSONObject result = new JSONObject();
        result =repertoryCheckPlanService.getInfo4PassJSON(checkPlanId);
        return ResponseEntity.ok().body(result.toJSONString());
    }



    @RequestMapping(value = "/checkPlanPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkPlanPass(@RequestBody Map requestMap,
                                         @AuthenticationPrincipal User user) throws Exception{

        String comment = (String) requestMap.get("comment");
        String manager = (String) requestMap.get("manager");
        String managerNote = (String) requestMap.get("managerNote");
        String finance = (String) requestMap.get("finance");
        String financeNote = (String) requestMap.get("financeNote");
        Long planId = new Long((String)requestMap.get("planId"));
        JSONObject result=repertoryCheckPlanService.setCheckPlanPass(user,planId,comment,manager,managerNote,finance,financeNote);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/checkFormPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkFormPass(@AuthenticationPrincipal User user,
                                                @RequestBody RepertoryCheckForm checkForm
                                                ) throws Exception{
        JSONObject result=repertoryCheckPlanService.setCheckFormPass(user,checkForm);
        return ResponseEntity.ok().body(result.toJSONString());
    }

}
