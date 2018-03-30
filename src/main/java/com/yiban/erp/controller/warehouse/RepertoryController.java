package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.entities.RepertoryInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.querybean.RepertoryQuery;
import com.yiban.erp.service.warehouse.RepertoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repertory")
public class RepertoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryController.class);

    @Autowired
    private RepertoryService repertoryService;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user, @RequestBody RepertoryQuery repertoryQuery) {
        repertoryQuery.setCompanyId(user.getCompanyId());
        List<RepertoryInfo> repertoryQueryList = repertoryInfoMapper.queryRepertory(repertoryQuery);
        return ResponseEntity.ok().body(JSON.toJSONString(repertoryQueryList));
    }

    @RequestMapping(value = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                       @RequestParam(name = "warehouseId") Integer warehouseId,
                                       @RequestParam(name = "goodId", required = false) Long goodId,
                                       @RequestParam(name = "goodSearch", required = false) String goodSearch,
                                       @RequestParam(name = "factoryId", required = false) Integer factoryId,
                                       @RequestParam(name = "page", required = false) Integer page,
                                       @RequestParam(name = "size", required = false) Integer size) throws Exception {
        Integer limit = size == null || size <= 0 ? 10 : size;
        Integer offset = (page == null || page <= 0) ? 0 : (page - 1) * limit;
        String search = StringUtils.isBlank(goodSearch) ? null: goodSearch.trim();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("companyId", user.getCompanyId());
        requestMap.put("warehouseId", warehouseId);
        requestMap.put("saleEnable", true);
        requestMap.put("goodId", goodId);
        requestMap.put("goodSearch", search);
        requestMap.put("factoryId", factoryId);
        requestMap.put("offset", offset);
        requestMap.put("limit", limit);
        List<RepertoryInfo> list = repertoryService.getSearchList(requestMap);
        int count = 0;
        if (!list.isEmpty()) {
            count = repertoryService.getSearchCount(requestMap);
        }
        JSONObject result = new JSONObject();
        result.put("data", list);
        result.put("count", count);
        return ResponseEntity.ok().body(result.toJSONString());
    }

}
