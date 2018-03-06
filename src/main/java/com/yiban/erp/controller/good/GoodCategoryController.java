package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodCategoryMapper;
import com.yiban.erp.entities.GoodCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

    /**
     * 获取产品类别的目录树
     *
     * @return
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tree() {
        List<GoodCategory> goodCategoryList = goodCategoryMapper.selectAll();
        JSONArray arr = new JSONArray();
        int i = 0;
        for (GoodCategory category : goodCategoryList) {
            JSONObject obj = new JSONObject();
            obj.put("title", category.getName());
            obj.put("loading", false);
            obj.put("id", category.getId());
            obj.put("expand", false);
            if (i == 0) {
                obj.put("selected", true);
            }
            obj.put("children", new JSONArray());
            arr.add(obj);
            i++;
        }
        return ResponseEntity.ok().body(arr.toString());
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
        String name = requestBody.getString("name");
        if (StringUtils.isEmpty(name)) {
            return ResponseEntity.badRequest().body("Empty name");
        }
        logger.info("DELETE good category:{}", name);

        int result = goodCategoryMapper.deleteByName(name);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Failed to delete");
    }
}