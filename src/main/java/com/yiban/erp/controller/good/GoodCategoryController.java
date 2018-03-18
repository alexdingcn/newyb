package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodCategoryMapper;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.entities.GoodCategory;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.ErrorCode;
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

import java.util.List;


@RestController
@RequestMapping("/good/category")
public class GoodCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(GoodCategoryController.class);

    @Autowired
    private GoodCategoryMapper goodCategoryMapper;

    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 获取产品类别的目录树
     *
     * @return
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tree(@AuthenticationPrincipal User user) {
        List<GoodCategory> goodCategoryList = goodCategoryMapper.selectAll(user.getCompanyId());
        JSONArray arr = new JSONArray();
        int i = 0;
        for (GoodCategory category : goodCategoryList) {
            JSONObject obj = new JSONObject();
            obj.put("title", category.getName());
            obj.put("loading", false);
            obj.put("id", category.getId());
            obj.put("expand", false);
            obj.put("children", new JSONArray());
            arr.add(obj);
            i++;
        }
        return ResponseEntity.ok().body(arr.toString());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user) throws Exception {
        List<GoodCategory> goodCategoryList = goodCategoryMapper.selectAll(user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(goodCategoryList));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody String payload) {
        JSONObject requestBody = JSON.parseObject(payload);
        logger.info("ADD new good category:{}", requestBody.getString("name"));
        GoodCategory goodCategory = new GoodCategory();
        goodCategory.setName(requestBody.getString("name"));
        goodCategory.setCreatedBy("admin");
        int result = goodCategoryMapper.insertSelective(goodCategory);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Failed to insert");
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String payload) {
        JSONObject requestBody = JSON.parseObject(payload);
        Integer id = requestBody.getInteger("id");
        logger.info("DELETE good category:{}", id);
        if (id <= 0) {
            return ResponseEntity.badRequest().body(ErrorCode.GOODS_CATEGORY_ID_MISSING.toString());
        }
        Long goodsCount = goodsMapper.selectCount(id, null, null);
        if (goodsCount > 0) {
            return ResponseEntity.badRequest().body(ErrorCode.GOODS_REMAINED_IN_CATEGORY.toString());
        }

        int result = goodCategoryMapper.deleteByPrimaryKey(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_DELETE_FROM_DB.toString());
    }
}