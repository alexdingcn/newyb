package com.yiban.erp.controller.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.CustomerCategoryMapper;
import com.yiban.erp.dao.CustomerMapper;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.entities.CustomerCategory;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/customer/category")
public class CustomerCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerCategoryController.class);

    @Autowired
    private CustomerCategoryMapper customerCategoryMapper;
    @Autowired
    private CustomerMapper customerMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> getList(@AuthenticationPrincipal User user) throws Exception {
        List<CustomerCategory> categories = customerCategoryMapper.getAllByCompanyId(user.getCompanyId());
        return ResponseEntity.ok().body((JSON) JSON.toJSON(categories));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody CustomerCategory category,
                                      @AuthenticationPrincipal User user) throws Exception {
        logger.info("get and request to add customer category:{} by user:{}",
                JSON.toJSONString(category), user.getId());
        category.setId(null);
        category.setCompanyId(user.getCompanyId());
        category.setCreateBy(user.getNickname());
        category.setCreateTime(new Date());
        if (category.getBatchDiscount() == null) {
            category.setBatchDiscount(BigDecimal.valueOf(100.00));
        }
        if (category.getRetailDiscount() == null) {
            category.setRetailDiscount(BigDecimal.valueOf(100.00));
        }
        int count = customerCategoryMapper.insertSelective(category);
        if (count > 0) {
            logger.info("user:{} success add a customer category id:", user.getId(), category.getId());
            return ResponseEntity.ok().build();
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody CustomerCategory category,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request update customer category:", user.getId(), JSON.toJSONString(category));
        if (category == null || category.getId() == null) {
            logger.warn(" request params error, category id must in params");
            return ResponseEntity.badRequest().body("Params error, id is need");
        }
        category.setUpdateTime(new Date());
        category.setUpdateBy(user.getNickname());
        int count = customerCategoryMapper.updateByPrimaryKeySelective(category);
        if (count > 0) {
            logger.info("user:{} success update customer category id:{}", user.getId(), category.getId());
            return ResponseEntity.ok().build();
        }else {
            logger.warn("user:{} update customer category:{} fail", user.getId(), category.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
    }

    @RequestMapping(value = "/remove/{categoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> remove(@PathVariable Integer categoryId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove customer category categoryId:{}", user.getId(), categoryId);
        if (categoryId == null) {
            throw new BizException(ErrorCode.CUSTOMER_CAT_DEL_PARAMS);
        }
        //先验证当前分组是否存在客户信息，如果存在，不能删除
        List<Customer> customers = customerMapper.getByCategoryId(user.getCompanyId(), categoryId);
        if (!customers.isEmpty()) {
            throw new BizException(ErrorCode.CUSTOMER_CAT_HAVE_CUST);
        }
        //验证当前分组是否有子分组，如果存在，不能删除
        List<CustomerCategory> categories = customerCategoryMapper.getByParentId(user.getCompanyId(), categoryId);
        if (!categories.isEmpty()) {
            throw new BizException(ErrorCode.CUSTOMER_CAT_HAVE_CAT);
        }
        int count = customerCategoryMapper.deleteByPrimaryKey(categoryId);
        if (count > 0) {
            logger.info("user:{} success remove customer category:{}", user.getId(), categoryId);
            return ResponseEntity.ok().build();
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
        }
    }



}
