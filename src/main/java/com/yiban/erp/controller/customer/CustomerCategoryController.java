package com.yiban.erp.controller.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.CustomerCategoryMapper;
import com.yiban.erp.dao.CustomerMapper;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.entities.CustomerCategory;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<JSON> getList() throws Exception {
        Integer companyId = 1;
        List<CustomerCategory> categories = customerCategoryMapper.getAllByCompanyId(companyId);
        return ResponseEntity.ok().body((JSON) JSON.toJSON(categories));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody CustomerCategory category) throws Exception {
        Integer companyId = 1;
        String userName = "admin";
        logger.info("get and request to add customer category:{} by user:{}",
                JSON.toJSONString(category), userName);
        category.setId(null);
        category.setCompanyId(companyId);
        category.setCreateBy(userName);
        category.setCreateTime(new Date());
        category.setUpdateBy(userName);
        category.setUpdateTime(new Date());
        int count = customerCategoryMapper.insertSelective(category);
        if (count > 0) {
            logger.info("user:{} success add a customer category id:", userName, category.getId());
            return ResponseEntity.ok().build();
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody CustomerCategory category) throws Exception {
        Integer companyId = 1;
        String userName = "admin";
        logger.info("user:{} request update customer category:", userName, JSON.toJSONString(category));
        if (category == null || category.getId() == null) {
            logger.warn(" request params error, category id must in params");
            return ResponseEntity.badRequest().body("Params error, id is need");
        }
        category.setUpdateTime(new Date());
        category.setUpdateBy(userName);
        int count = customerCategoryMapper.updateByPrimaryKeySelective(category);
        if (count > 0) {
            logger.info("user:{} success update customer category id:{}", userName, category.getId());
            return ResponseEntity.ok().build();
        }else {
            logger.warn("user:{} update customer category:{} fail", userName, category.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
    }

    @RequestMapping(value = "/remove/{categoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> remove(@PathVariable Integer categoryId) throws Exception {
        String userName = "admin";
        Integer companyId = 1;
        logger.info("user:{} request remove customer category categoryId:{}", userName, categoryId);
        if (categoryId == null) {
            return ResponseEntity.badRequest().body("Request params error");
        }
        //先验证当前分组是否存在客户信息，如果存在，不能删除
        List<Customer> customers = customerMapper.getByCategoryId(companyId, categoryId);
        if (!customers.isEmpty()) {
            throw new BizRuntimeException(ErrorCode.CUSTOMER_CAT_HAVE_CUST);
        }
        //验证当前分组是否有子分组，如果存在，不能删除
        List<CustomerCategory> categories = customerCategoryMapper.getByParentId(companyId, categoryId);
        if (!categories.isEmpty()) {
            throw new BizRuntimeException(ErrorCode.CUSTOMER_CAT_HAVE_CAT);
        }
        int count = customerCategoryMapper.deleteByPrimaryKey(categoryId);
        if (count > 0) {
            logger.info("user:{} success remove customer category:{}", userName, categoryId);
            return ResponseEntity.ok().build();
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
        }
    }



}
