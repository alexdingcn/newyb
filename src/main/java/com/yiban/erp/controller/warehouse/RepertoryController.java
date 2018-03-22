package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.RepertoryInfo;
import com.yiban.erp.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repertory")
public class RepertoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryController.class);

    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private GoodsMapper goodsMapper;

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
        List<RepertoryInfo> list = repertoryInfoMapper.getDetailList(user.getCompanyId(), warehouseId, true,
                goodId, search, factoryId, offset, limit);
        int count = 0;
        if (!list.isEmpty()) {
            List<Long> goodIdList = new ArrayList<>();
            list.stream().forEach(item -> goodIdList.add(item.getGoodId()));
            List<Goods> goodsList = goodsMapper.selectByIdList(goodIdList);
            Map<Long, List<Goods>> map = goodsList.stream().collect(Collectors.groupingBy(Goods::getId));
            list.stream().forEach(item -> {
                List<Goods> goodItem = map.get(item.getGoodId());
                item.setGoods(goodItem != null ? goodItem.get(0) : null);
            });
            count = repertoryInfoMapper.getDetailListCount(user.getCompanyId(), warehouseId, true,
                    goodId, search, factoryId);
        }
        JSONObject result = new JSONObject();
        result.put("data", list);
        result.put("count", count);
        return ResponseEntity.ok().body(result.toJSONString());
    }

}
