package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.BuyOrderStatus;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.RepertoryCheckDetailMapper;
import com.yiban.erp.dao.RepertoryCheckMapper;
import com.yiban.erp.dao.RepertoryCheckPartMapper;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.warehouse.RepertoryService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
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
    private RepertoryCheckMapper repertoryCheckMapper;
    @Autowired
    private RepertoryCheckDetailMapper repertoryCheckDetailMapper;
    @Autowired
    private RepertoryService repertoryService;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private RepertoryCheckPartMapper repertoryCheckPartMapper;

    //根据条件获取入库单list
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                       @RequestParam(name = "checkType", required = false) Integer checkType,
                                       @RequestParam(name = "counterState", required = false) Integer counterState,
                                       @RequestParam(name = "warehouseId",required = false) Integer warehouseId,
                                       @RequestParam(name = "startDate", required = false) Date startDate,
                                       @RequestParam(name = "endDate", required = false) Date endDate
                                       ) {


        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkType", checkType);
        requestMap.put("counterState", counterState);
        requestMap.put("warehouseId", warehouseId);
        requestMap.put("startDate", startDate);
        requestMap.put("endDate", endDate);
        List<RepertoryCheck> list = repertoryCheckMapper.getCheckList(requestMap);

        JSONObject result = new JSONObject();
        result.put("data", list);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderPartList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                       @RequestParam(name = "checkOrderId", required = true) long checkOrderId
    ) {


        RepertoryCheck checkinfo= repertoryCheckMapper.selectByPrimaryKey(checkOrderId);
        List<RepertoryCheckPart>  partlist = repertoryCheckPartMapper.selectByCheckOrderId(checkOrderId);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        result.put("checkPartList", partlist);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfo(@AuthenticationPrincipal User user,
                                            @RequestParam(name = "checkOrderId", required = true) Long checkOrderId
    ) {

        RepertoryCheck checkinfo= repertoryCheckMapper.selectByPrimaryKey(checkOrderId);

        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    //根据条件获取入库单list
    @RequestMapping(value = "/orderinfoList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderinfoList(@AuthenticationPrincipal User user,
                                            @RequestParam(name = "checkOrderId", required = true) Long checkOrderId
    ) {

        RepertoryCheck checkinfo= repertoryCheckMapper.selectByPrimaryKey(checkOrderId);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkOrderId", checkOrderId);
        List<RepertoryCheckDetail>  cdlist=repertoryCheckDetailMapper.getCheckDetailList(requestMap);
        JSONObject result = new JSONObject();
        result.put("data", checkinfo);
        result.put("checkDetailList", cdlist);
        return ResponseEntity.ok().body(result.toJSONString());
    }
   //
    @Transactional
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
                                      @RequestBody RepertoryCheck repertoryCheck) {
        logger.info("ADD new repertoryCheck order, userId={}", user.getId());

        //根据盘点类型验证上传参数
        Integer checkType=repertoryCheck.getCheckType();

        //库存盘点--0
        if(checkType==0){
            //根据仓库ID,库区,获取所有库存商品进行盘点
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("companyId", user.getCompanyId());
            requestMap.put("warehouseId", repertoryCheck.getWarehouseId());
            requestMap.put("counterState", repertoryCheck.getCounterState());
            //当前库存数量大于0
            requestMap.put("limitCheck", 0);
            //1.查询符合盘点条件的商品
            List<RepertoryInfo> checklist = repertoryService.getSearchList(requestMap);

            if(checklist==null || checklist.size()<=0){
                return ResponseEntity.badRequest().body(ErrorCode.CHECK_ORDER_NO_GOODS.toString());
            }
            //2.创建盘点单
            RepertoryCheck rcheck=new RepertoryCheck();
            rcheck.setCompanyId(user.getCompanyId());
            rcheck.setWarehouseId(repertoryCheck.getWarehouseId());
            rcheck.setCheckType(checkType);
            rcheck.setMakeUserId(user.getId());
            rcheck.setCheckDate(new Date());
            rcheck.setCheckCode(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.INVENTORY));
            rcheck.setCounterState(repertoryCheck.getCounterState());
            rcheck.setCreatedBy(user.getNickname());
            rcheck.setNote(repertoryCheck.getNote());
            rcheck.getCreatedTime(new Date());
            rcheck.setState(0);
            repertoryCheckMapper.insert(rcheck);

            for(int i=0;i<checklist.size();i++){
                RepertoryInfo tempGoods=checklist.get(i);
                RepertoryCheckDetail rcheckDetail=new RepertoryCheckDetail();
                rcheckDetail.setBatchCode(tempGoods.getBatchCode());
                rcheckDetail.setAccAmount(new BigDecimal(tempGoods.getQuantity()));        //账面数量必定大于0
                rcheckDetail.setCheckAmount(new BigDecimal(0));
                rcheckDetail.setCompanyId(tempGoods.getCompanyId());
                rcheckDetail.setWarehouseId(tempGoods.getWarehouseId());
                rcheckDetail.setCreatedBy(user.getNickname());
                rcheckDetail.setCreatedTime(new Date());
                rcheckDetail.setPrice(tempGoods.getBuyPrice());
                rcheckDetail.setGoodId(tempGoods.getGoodsId());
                rcheckDetail.setCheckId(rcheck.getId());
                rcheckDetail.setRepertoryInfoId(tempGoods.getId());
                repertoryCheckDetailMapper.insert(rcheckDetail);
            }
            JSONObject obj = new JSONObject();
            return ResponseEntity.ok().body(obj.toString());

        }else{
            return ResponseEntity.badRequest().body("暂未开放");
        }
    }

}
