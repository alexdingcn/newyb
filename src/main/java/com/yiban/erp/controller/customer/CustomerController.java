package com.yiban.erp.controller.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.CustomerMapper;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.entities.CustomerCert;
import com.yiban.erp.entities.CustomerRep;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.*;
import com.yiban.erp.service.customer.CustomerService;
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
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<JSON> list(@RequestParam(name = "page", required = false) Integer page,
                                     @RequestParam(name = "size", required = false) Integer size,
                                     @RequestParam(name = "categoryId", required = false) Integer reqCategoryId,
                                     @RequestParam(name = "customerName", required = false) String reqCustomerName,
                                     @RequestParam(name = "customerNo", required = false) String reqCustomerNo,
                                     @RequestParam(name= "shorName", required = false) String reqShorName,
                                     @AuthenticationPrincipal User user)  throws Exception {
        logger.info("get customer list page:{}, size:{}, categoryId:{}, customerName:{}, customerNo:{}, shorName:{}",
                page, size, reqCategoryId, reqCustomerName, reqCustomerNo, reqShorName);
        Integer categoryId = reqCategoryId == null || reqCategoryId <=0 ? null : reqCategoryId;
        String customerName = null;
        String customerNo = null;
        String shorName = null;
        if (reqCustomerName != null && !"".equals(reqCustomerName.trim())) {
            customerName = reqCustomerName.trim();
        }
        if (reqCustomerNo != null && !"".equals(reqCustomerNo.trim())) {
            customerNo = reqCustomerNo.trim();
        }
        if (reqShorName != null && !"".equals(reqShorName.trim())) {
            shorName = reqShorName.trim();
        }
        Integer pageSize = size == null ? 10 : size;
        Integer offset = (page == null || page <= 0 ? 0 : page - 1) * pageSize;

        int count = customerMapper.selectAllCount(user.getCompanyId(), categoryId, customerName, customerNo, shorName);
        List<Customer> customers = customerMapper.selectAll(user.getCompanyId(), categoryId, customerName, customerNo, shorName, pageSize, offset);
        JSONObject result = new JSONObject();
        result.put("count", count);
        result.put("data", customers);
        return ResponseEntity.ok().body((JSON) result);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<String> delete(@RequestBody String reqData,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request delete customer, request params:{}", user.getId(), reqData);
        List<Integer> deleteIds = JSON.parseArray(reqData, Integer.class);
        if (deleteIds.isEmpty()) {
            throw new BizException(ErrorCode.CUSTOMER_DEL_PARAMS_EMPTY);
        }
        int delCount = customerMapper.deleteByIdList(deleteIds, user.getCompanyId(), user.getNickname(), new Date());
        JSONObject result = new JSONObject();
        result.put("count", delCount);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody Customer customer,
                                      @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request add customer.", user.getId());
        customer.setCompanyId(user.getCompanyId());
        customer.setCreateBy(user.getNickname());
        Customer result = customerService.addCustomer(customer);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody Customer customer,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request update customer:{}", user.getId(), customer.getId());
        customer.setUpdateBy(user.getNickname());
        Customer result = customerService.updateCustomer(customer);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/cert/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> getCertList(@RequestParam("customerId") Integer customerId) throws Exception {
        logger.debug("request get customer:{} cert list", customerId);
        List<CustomerCert> certs = customerService.getCertList(customerId);
        return ResponseEntity.ok().body((JSON) JSON.toJSON(certs));
    }

    @RequestMapping(value = "/cert/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> certAdd(@RequestBody CustomerCert cert,
                                          @AuthenticationPrincipal User user) throws Exception {
        cert.setCreateBy(user.getNickname());
        logger.info("user:{} request to add customer cert, customerId:", user.getId(), cert.getCustomerId());
        CustomerCert result = customerService.addCert(cert);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/cert/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> certUpdate(@RequestBody CustomerCert cert,
                                             @AuthenticationPrincipal User user) throws Exception {
        cert.setUpdateBy(user.getNickname());
        logger.info("user:{} request to update customer cert, id:", user.getId(), cert.getId());
        CustomerCert result = customerService.updateCert(cert);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/cert/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> certRemove(@RequestBody String removeIds,
                                             @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request to remove customer cert, ids:", user.getId(), removeIds);
        List<Integer> idList = JSON.parseArray(removeIds, Integer.class);
        int count = customerService.removeCerts(idList);
        JSONObject result = new JSONObject();
        result.put("count", count);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/rep/list", method = RequestMethod.GET)
    public ResponseEntity<JSON> getRepList(@RequestParam("customerId") Integer customerId) throws Exception {
        logger.debug("get an request to get customer rep list by customerId:", customerId);
        List<CustomerRep> reps = customerService.getRepList(customerId);
        return ResponseEntity.ok().body((JSON) JSON.toJSON(reps));
    }

    @RequestMapping(value = "/rep/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> repAdd(@RequestBody CustomerRep rep,
                                         @AuthenticationPrincipal User user) throws Exception {
        rep.setCreateBy(user.getNickname());
        logger.info("user:{} add customer rep.", user.getId());
        CustomerRep result = customerService.addRep(rep);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/rep/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> repUpdate(@RequestBody CustomerRep rep,
                                            @AuthenticationPrincipal User user) throws Exception {
        rep.setUpdateBy(user.getNickname());
        logger.info("user:{} update customer rep:{}", user.getId(), rep.getId());
        CustomerRep result = customerService.updateRep(rep);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/rep/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> repRemove(@RequestBody String removeIds,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request to remove customer rep, ids:", user.getId(), removeIds);
        List<Integer> idList = JSON.parseArray(removeIds, Integer.class);
        int count = customerService.removeReps(idList);
        JSONObject result = new JSONObject();
        result.put("count", count);
        return ResponseEntity.ok().body(result.toJSONString());
    }


}
