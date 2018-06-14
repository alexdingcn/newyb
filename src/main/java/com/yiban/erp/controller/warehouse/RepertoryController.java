package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.dto.RepertoryQuery;
import com.yiban.erp.dto.RepertorySelectQuery;
import com.yiban.erp.entities.RepertoryInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.warehouse.RepertoryService;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/repertory")
public class RepertoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryController.class);

    @Autowired
    private RepertoryService repertoryService;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;


    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                       @RequestBody RepertoryQuery repertoryQuery) {
        JSONObject result = repertoryService.getCurrentRepertory(user, repertoryQuery);
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }


    @RequestMapping(value = "/select", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> selectList(@RequestBody RepertorySelectQuery query,
                                             @AuthenticationPrincipal User user) throws Exception {
        logger.info("select as RepertorySelectQuery {}" ,query);

        query.setCompanyId(user.getCompanyId());
        query.setByPage(true);
        Integer count = 0;
        if (query.isUseBatchCode()) {
            count = repertoryInfoMapper.querySelectCount(query);
        } else {
            count = repertoryInfoMapper.queryCountGroupByGoods(query);
        }

        List<RepertoryInfo> infos = new ArrayList<>();
        if (count == null || count <= 0) {
            count = 0;
        } else {
            if (query.isUseBatchCode()) {
                infos = repertoryService.querySelectList(query);
            } else {
                infos = repertoryService.queryListGroupByGoods(query);
            }
        }
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("data", infos);
        return ResponseEntity.ok().body(JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/refOrder/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getListByRefOrder(@RequestBody RepertorySelectQuery query,
                                                    @AuthenticationPrincipal User user) {
        query.setCompanyId(user.getCompanyId());
        List<RepertoryInfo> result = repertoryService.getListByRefOrder(query);
        return ResponseEntity.ok().body(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

}
