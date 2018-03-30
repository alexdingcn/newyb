package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.RepertoryCounterStatus;
import com.yiban.erp.constant.RepertorySaleStatus;
import com.yiban.erp.constant.RepertoryStoreStatus;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.entities.RepertoryInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.querybean.RepertoryQuery;
import com.yiban.erp.service.warehouse.RepertoryService;
import com.yiban.erp.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
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

        Integer pageSize = repertoryQuery.getSize() == null ? 10 : repertoryQuery.getSize();
        Integer offset = (repertoryQuery.getPage() == null || repertoryQuery.getPage() <= 0 ? 0 : repertoryQuery.getPage() - 1) * pageSize;
        repertoryQuery.setOffset(offset);
        repertoryQuery.setSize(pageSize);
        repertoryQuery.setCompanyId(user.getCompanyId());
        //库龄大于x天数 根据库龄设置最大一个入库时间，查询所有早于这个时间的数据
        Integer keepdays=repertoryQuery.getKeedays();
        if(keepdays!=null && keepdays>0){
            keepdays=keepdays*-1;
            Date max_in_date=new Date();
            max_in_date=new DateUtil().getPreDate(max_in_date,"d",keepdays);
            repertoryQuery.setMax_in_date(max_in_date);
        }
        if (RepertoryCounterStatus.ALL.name().equalsIgnoreCase(repertoryQuery.getCounter_state())) {
            repertoryQuery.setCounter_state(null);
        }
        if (RepertoryStoreStatus.ALL.name().equalsIgnoreCase(repertoryQuery.getStore_state())) {
            repertoryQuery.setStore_state(null);
        }
        if (RepertorySaleStatus.ALL.name().equalsIgnoreCase(repertoryQuery.getSale_state())) {
            repertoryQuery.setSale_state(null);
        }
        List<RepertoryInfo> list = repertoryInfoMapper.queryRepertoryPage(repertoryQuery);
        int count = 0;
        if (!list.isEmpty()) {
            count =repertoryInfoMapper.queryRepertoryCount(repertoryQuery);
        }
        JSONObject result = new JSONObject();
        result.put("data", list);
        result.put("total", count);
        return ResponseEntity.ok().body(result.toJSONString());
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
