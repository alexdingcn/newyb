package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodCategoryMapper;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.entities.GoodCategory;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
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

import java.util.Date;
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
    public ResponseEntity<String> add(@RequestBody String payload,
                                      @AuthenticationPrincipal User user) throws Exception {
        JSONObject requestBody = JSON.parseObject(payload);
        String name = requestBody.getString("name");
        logger.info("user:{} ADD new good category:{}", user.getId(), name);
        if (StringUtils.isBlank(name)) {
            throw new BizException(ErrorCode.GOODS_CATEGORY_NAME_MISS);
        }
        GoodCategory goodCategory = new GoodCategory();
        goodCategory.setCompanyId(user.getCompanyId());
        goodCategory.setName(requestBody.getString("name"));
        goodCategory.setCreatedBy(user.getNickname());
        goodCategory.setCreatedTime(new Date());
        int result = goodCategoryMapper.insertSelective(goodCategory);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> edit(@RequestBody GoodCategory goodCategory,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} update goods category:{} name:{}", user.getId(), goodCategory.getId(), goodCategory.getName());
        if (StringUtils.isBlank(goodCategory.getName())) {
            throw new BizException(ErrorCode.GOODS_CATEGORY_NAME_MISS);
        }
        goodCategory.setUpdatedBy(user.getNickname());
        goodCategory.setUpdatedTime(new Date());
        int count = goodCategoryMapper.updateByPrimaryKeySelective(goodCategory);
        if (count > 0) {
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String payload,
                                         @AuthenticationPrincipal User user) throws Exception {
        JSONObject requestBody = JSON.parseObject(payload);
        Integer id = requestBody.getInteger("id");
        logger.info("user:{} DELETE good category:{}", user.getId(), id);
        if (id <= 0) {
            throw new BizException(ErrorCode.GOODS_CATEGORY_ID_MISSING);
        }
        Long goodsCount = goodsMapper.selectCount(user.getCompanyId(), id, null, null);
        if (goodsCount > 0) {
            throw new BizException(ErrorCode.GOODS_REMAINED_IN_CATEGORY);
        }

        int result = goodCategoryMapper.deleteByPrimaryKey(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
    }
}