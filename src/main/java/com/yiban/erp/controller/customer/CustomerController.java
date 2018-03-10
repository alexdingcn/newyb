package com.yiban.erp.controller.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.CustomerMapper;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerMapper customerMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<JSON> list(@RequestParam(name = "page", required = false) Integer page,
                                     @RequestParam(name = "size", required = false) Integer size,
                                     @RequestParam(name = "categoryId", required = false) Integer reqCategoryId,
                                     @RequestParam(name = "customerName", required = false) String reqCustomerName,
                                     @RequestParam(name = "customerNo", required = false) String reqCustomerNo)  throws Exception {
        logger.info("get customer list page:{}, size:{}, categoryId:{}, customerName:{}, customerNo:{}",
                page, size, reqCategoryId, reqCustomerName, reqCustomerNo);
        Integer companyId = 1;

        Integer categoryId = reqCategoryId == null || reqCategoryId <=0 ? null : reqCategoryId;
        String customerName = null;
        String customerNo = null;
        if (reqCustomerName != null && !"".equals(reqCustomerName.trim())) {
            customerName = reqCustomerName.trim();
        }
        if (reqCustomerNo != null && !"".equals(reqCustomerNo.trim())) {
            customerNo = reqCustomerNo.trim();
        }
        Integer pageSize = size == null ? 10 : size;
        Integer offset = (page == null || page <= 0 ? 0 : page - 1) * pageSize;

        int count = customerMapper.selectAllCount(companyId, categoryId, customerName, customerNo);
        List<Customer> customers = customerMapper.selectAll(companyId, categoryId, customerName, customerNo, pageSize, offset);

        JSONObject result = new JSONObject();
        result.put("count", count);
        result.put("data", customers);
        return ResponseEntity.ok().body((JSON) result);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<String> delete(@RequestBody String reqData) throws Exception {
        Integer companyId = 1;
        String userName = "admin";
        logger.info("user:{} request delete customer, request params:{}", userName, reqData);
        List<Integer> deleteIds = JSON.parseArray(reqData, Integer.class);
        if (deleteIds.isEmpty()) {
            throw new BizException(ErrorCode.CUSTOMER_DEL_PARAMS_EMPTY);
        }
        int delCount = customerMapper.deleteByIdList(deleteIds, companyId, userName, new Date());
        JSONObject result = new JSONObject();
        result.put("count", delCount);
        return ResponseEntity.ok().body(result.toJSONString());
    }




}
