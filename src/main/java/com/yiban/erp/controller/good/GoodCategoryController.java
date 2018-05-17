package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.GoodCategoryMapper;
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
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/goods/category")
public class GoodCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(GoodCategoryController.class);

    @Autowired
    private GoodCategoryMapper goodCategoryMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user) throws Exception {
        List<GoodCategory> goodCategoryList = goodCategoryMapper.selectAll(user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(goodCategoryList));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody GoodCategory category,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save goods category:{}", user.getId(), JSON.toJSONString(category));
        if (StringUtils.isEmpty(category.getName())) {
            throw new BizException(ErrorCode.GOODS_CATEGORY_NAME_MISS);
        }
        if (category.getId() != null) {
            //修改
            category.setUpdatedBy(user.getNickname());
            category.setUpdatedTime(new Date());
            goodCategoryMapper.updateByPrimaryKeySelective(category);
        }else {
            //新建
            category.setCompanyId(user.getCompanyId());
            if (category.getParent() == null) {
                category.setParent(-1);
            }
            category.setCreatedBy(user.getNickname());
            category.setCreatedTime(new Date());
            goodCategoryMapper.insert(category);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/remove/{categoryId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Integer categoryId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} DELETE good category:{}", user.getId(), categoryId);
        if (categoryId <= 0) {
            throw new BizException(ErrorCode.GOODS_CATEGORY_ID_MISSING);
        }
        //是否有子分类
        Integer subCount = goodCategoryMapper.subCategoryCount(categoryId);
        if (subCount > 0) {
            throw new BizException(ErrorCode.GOODS_CHILD_IN_CATEGORY);
        }
        //是否有商品
        Integer goodsCount = goodCategoryMapper.goodsInfoUseCategory(categoryId);
        if (goodsCount > 0) {
            throw new BizException(ErrorCode.GOODS_REMAINED_IN_CATEGORY);
        }
        int result = goodCategoryMapper.deleteByPrimaryKey(categoryId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
    }
}