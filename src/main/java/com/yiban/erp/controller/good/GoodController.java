package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.OptionsMapper;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.Options;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/goods")
public class GoodController {
    private static final Logger logger = LoggerFactory.getLogger(GoodController.class);

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                        @RequestParam(required = false) Integer catId,
                                       @RequestParam(required = false) String search,
                                       @RequestParam(required = false) Integer factoryId,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size) {
        logger.info("Get goods list, search={}, catId={}, page={}, size={}", search, catId, page, size);
        Integer pageSize = size == null ? 10 : size;
        Integer offset = (page == null || page <= 0 ? 0 : page - 1) * pageSize;

        Long count = goodsMapper.selectCount(user.getCompanyId(), catId, factoryId, search);
        List<Goods> goodsList = new ArrayList<>();
        if (count > 0) {
            goodsList = goodsMapper.selectAll(user.getCompanyId(), catId, factoryId, search, offset, pageSize);
            goodsService.setGoodsOptionName(goodsList);
        }
        JSONObject result = new JSONObject();
        result.put("total", count);
        result.put("data", JSON.toJSON(goodsList));
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/{goodsId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        goodsService.setGoodsOptionName(goods);
        return ResponseEntity.ok().body(JSON.toJSONString(goods));
    }

    @RequestMapping(value = "/remove/{goodsId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Long goodsId) {
        int result = goodsMapper.deleteByPrimaryKey(goodsId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody Goods goods,
                                      @AuthenticationPrincipal User user) {
        logger.info("save add or update goods :{}", goods);
        int result = 0;
        if (goods.getId() == null) {
            goods.setCompanyId(user.getCompanyId());
            goods.setCreatedBy(user.getNickname());
            goods.setCreatedTime(new Date());
            result = goodsMapper.insertSelective(goods);
        } else {
            if (goods.getCompanyId() == null) {
                goods.setCompanyId(user.getCompanyId());
            }else {
                if (!goods.getCompanyId().equals(user.getCompanyId())) {
                    throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
                }
            }
            goods.setUpdatedTime(new Date());
            goods.setUpdatedBy(user.getNickname());
            result = goodsMapper.updateByPrimaryKeySelective(goods);
        }
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }
}