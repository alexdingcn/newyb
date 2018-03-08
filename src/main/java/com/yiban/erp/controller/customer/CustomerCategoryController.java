package com.yiban.erp.controller.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.CustomerCategoryMapper;
import com.yiban.erp.entities.CustomerCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/customer/category")
public class CustomerCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerCategoryController.class);

    @Autowired
    private CustomerCategoryMapper customerCategoryMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> getList() {
        Integer companyId = 1;
        List<CustomerCategory> categories = customerCategoryMapper.getAllByCompanyId(companyId);
        JSONObject result = new JSONObject();
        result.put("data", categories);
        return ResponseEntity.ok().body((JSON) result);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody CustomerCategory category) {
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
            logger.warn("user:{} fail insert customer category.", userName);
            return ResponseEntity.badRequest().body("Fail to insert");
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody CustomerCategory category) {
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
            return ResponseEntity.badRequest().body("Update data fail");
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String reqData) {
        String userName = "admin";
        logger.info("user:{} request remove customer category reqData:{}", userName, reqData);
        JSONObject reqObj = JSON.parseObject(reqData);
        Integer categoryId = reqObj.getInteger("categoryId");
        if (categoryId == null) {
            return ResponseEntity.badRequest().body("Request params error");
        }
        int count = customerCategoryMapper.deleteByPrimaryKey(categoryId);
        if (count > 0) {
            logger.info("user:{} success remove customer category:{}", userName, categoryId);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().body("Fail to delete");
        }
    }



}
