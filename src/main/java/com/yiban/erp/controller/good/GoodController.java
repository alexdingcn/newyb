package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.entities.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/goods")
public class GoodController {
    private static final Logger logger = LoggerFactory.getLogger(GoodController.class);

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取产品的目录树
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestParam(required = false) Integer catId,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size) {
        logger.info("Get goods list, catId={}, page={}, size={}", catId, page, size);
        Integer pageSize = size == null ? 10 : size;
        Integer offset = (page == null || page <= 0 ? 0 : page - 1) * pageSize;

        Long count = goodsMapper.selectCount(catId);
        List<Goods> goodsList = goodsMapper.selectAll(catId, offset, pageSize);
        JSONObject result = new JSONObject();
        result.put("total", count);
        result.put("data", JSON.toJSON(goodsList));
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/{goodsId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        return ResponseEntity.ok().body(JSON.toJSONString(goods));
    }

    @RequestMapping(value = "/remove/{goodsId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Long goodsId) {
        int result = goodsMapper.deleteByPrimaryKey(goodsId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Failed to delete");
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody Goods goods) {
        logger.info("ADD new good category:{}", goods);
        int result = 0;
        if (goods.getId() == null) {
            result = goodsMapper.insertSelective(goods);
        } else {
            result = goodsMapper.updateByPrimaryKey(goods);
        }
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Failed to insert");
    }
}