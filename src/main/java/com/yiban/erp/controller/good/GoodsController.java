package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.GoodsInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.goods.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.*;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据详情信息，全部展开商品详情信息
     * @param user
     * @param catId
     * @param search
     * @param page
     * @param size
     * @param options 辅助查询项，空值是否查询库存，最近一次销售价，最近一次采购价，多项以 ';'分割
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                       @RequestParam(required = false) Integer catId,
                                       @RequestParam(required = false) String search,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size,
                                       @RequestParam(required = false) String options,
                                       @RequestParam(required = false) Integer warehouseId) {
        logger.info("Get goods list, search={}, catId={}, page={}, size={}, options={}, warehouseId={}",
                search, catId, page, size, options, warehouseId);
        if (StringUtils.isEmpty(search)) {
            search = null;
        }
        List<String> optionList = new ArrayList<>();
        if (StringUtils.isNotEmpty(options)) {
            String[] opList = options.split(";");
            optionList = Arrays.asList(opList);
        }
        GoodsQuery query = new GoodsQuery();
        query.setCompanyId(user.getCompanyId());
        query.setCategoryId(catId);
        query.setSearch(search);
        query.setPage(page);
        query.setPageSize(size);
        query.setOptions(optionList);
        query.setWarehouseId(warehouseId);

        Long count = 0L;
        count = goodsService.getChooseListDetailCount(query);
        List<Goods> details = new ArrayList<>();
        if (count > 0) {
            details = goodsService.getChooseListDetail(query);
        }
        JSONObject result = new JSONObject();
        result.put("total", count);
        result.put("data", JSON.toJSON(details));
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchList(@RequestBody GoodsQuery query,
                                             @AuthenticationPrincipal User user) {
        query.setCompanyId(user.getCompanyId());
        query.setDefaultAttr(goodsService.getDefaultAttrRef(user.getCompanyId()));
        Long count = goodsService.searchListCount(query);
        List<GoodsInfo> result = new ArrayList<>();
        if (count > 0) {
            result = goodsService.searchList(query);
        }else if (count == null){
            count = 0L;
        }
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("data", result);
        return ResponseEntity.ok(response.toJSONString());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long id) throws Exception {
        GoodsInfo goodsInfo = goodsService.getGoodsInfoById(id);
        return ResponseEntity.ok().body(JSON.toJSONString(goodsInfo));
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getInfoByDetailId(@RequestParam("detailId") Long detailId,
                                                    @AuthenticationPrincipal User user) throws Exception {
        Goods goods = goodsService.getInfoByDetailId(detailId, user);
        return ResponseEntity.ok().body(JSON.toJSONString(goods));
    }

    @RequestMapping(value = "/remove/{infoId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Long infoId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request to remove goods info by id:{}", user.getId(), infoId);
        goodsService.remove(infoId, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody GoodsInfo goodsInfo,
                                      @AuthenticationPrincipal User user) throws Exception {
        logger.info("save add or update goods info :{}", JSON.toJSONString(goodsInfo));
        goodsService.saveGoodsInfo(goodsInfo, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/copy/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> copy(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request copy goods info by modal id:{}", user.getId(), id);
        goodsService.copyGoodsInfo(id, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/detail/remove/{detailId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detailRemove(@PathVariable Long detailId,
                                               @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove detail by id:{}", user.getId(), detailId);
        goodsService.detailRemoveById(detailId, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/defaultAttr", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDefaultAttr(@AuthenticationPrincipal User user){
        Integer id=user.getCompanyId();
        return ResponseEntity.ok().body(JSON.toJSONString(goodsService.getDefaultAttr(id)));
    }
}