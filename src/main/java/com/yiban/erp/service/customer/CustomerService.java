package com.yiban.erp.service.customer;

import com.yiban.erp.constant.CustomerStatus;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.CustomerMapper;
import com.yiban.erp.dao.CustomerRepMapper;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.entities.CustomerRep;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerRepMapper customerRepMapper;

    public Customer getDetail(Integer companyId, Long id) {
        Customer customer = customerMapper.getCustomerDetailById(companyId, id);
        if (customer != null) {
            //设置代表人的使用默认标识
            setRepDefaultFlag(customer.getCustomerReps());
        }
        return customer;
    }

    public Customer addCustomer(Customer reqCustomer) throws BizException {
        Customer addCustomer = reqCustomer;
        if (!validateCust(reqCustomer, true)) {
            logger.warn("add customer validate fail.");
            throw new BizException(ErrorCode.CUSTOMER_REQUIRE_PARAMS_ERROR);
        }
        addCustomer.setCustomerNo(UtilTool.makeOrderNumber(reqCustomer.getCompanyId(), OrderNumberType.CUST));
        addCustomer.setStatus(CustomerStatus.NORMAL.name());
        addCustomer.setCreateTime(new Date());
        int count = customerMapper.insert(addCustomer);
        if (count > 0 && addCustomer.getId() > 0) {
            return addCustomer;
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    public Customer updateCustomer(Customer reqCustomer) throws BizException {
        Customer updCustomer = reqCustomer;
        if (!validateCust(updCustomer, false) || updCustomer.getId() == null) {
            logger.warn("update customer validate fail. customerId:", updCustomer.getId());
            throw new BizException(ErrorCode.CUSTOMER_REQUIRE_PARAMS_ERROR);
        }
        updCustomer.setUpdateTime(new Date());
        int count = customerMapper.updateByPrimaryKeySelective(updCustomer);
        if (count > 0) {
            return updCustomer;
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
    }

    private boolean validateCust(Customer customer, boolean validCompany) {
        if (customer == null) {
            return false;
        }
        if (validCompany) {
            logger.debug("need validate company id.");
            if (customer.getCompanyId() == null || customer.getCompanyId() < 0) {
                logger.warn("add validate customer company id is error. companyId:{}", customer.getCompanyId());
                return false;
            }
        }
        if (customer.getCategoryId() == null || customer.getCategoryId() < 0) {
            logger.warn("add validate customer category id fail. categoryId:{}", customer.getCategoryId());
            return false;
        }
        if (customer.getName() == null) {
            logger.warn("add validate customer name is null.");
            return false;
        }
        return true;
    }


    public CustomerRep getDefaultCustomerRep(Long customerId) {
        CustomerRep rep = customerRepMapper.getDefault(customerId);
        if (rep != null) {
            rep.setIsDefault(true);
            if (rep.getDefaultTime() == null) {
                //没有设置过，直接设置为当前值
                rep.setDefaultTime(new Date());
                customerRepMapper.setDefault(rep.getId(), rep.getDefaultTime());
            }
            return rep;
        }else {
            logger.info("get customer default rep fail. customerId:{}", customerId);
            return null;
        }
    }

    public int setDefaultCustomerRep(Integer customerRepId) {
        return customerRepMapper.setDefault(customerRepId, new Date());
    }

    private void setRepDefaultFlag(List<CustomerRep> reps) {
        if (reps == null || reps.isEmpty()) {
            return;
        }
        //先按客户好分组
        Map<Long, List<CustomerRep>> tempMap = reps.stream()
                .collect(Collectors.groupingBy(CustomerRep::getCustomerId));
        for (Map.Entry<Long, List<CustomerRep>> entry : tempMap.entrySet()) {
            Long customerId = entry.getKey();
            List<CustomerRep> list = entry.getValue();
            CustomerRep defaultValue = getDefaultCustomerRep(customerId);
            if (defaultValue == null || list.isEmpty()) {
                continue;
            }
            for (CustomerRep rep : list) {
                if (defaultValue.getId().equals(rep.getId())) {
                    rep.setIsDefault(true);
                }else {
                    rep.setIsDefault(false);
                }
            }
        }
    }

    public List<CustomerRep> getRepList(Long customerId, Boolean enableOnly) throws BizException {
        if (customerId == null) {
            return Collections.emptyList();
        }
        List<CustomerRep> reps = customerRepMapper.getByCustomerId(customerId, enableOnly);
        //设置默认标识
        setRepDefaultFlag(reps);
        return reps;
    }

    public CustomerRep addRep(CustomerRep rep) throws BizException {
        CustomerRep reqRep = rep;
        if (!validateRep(reqRep)) {
            logger.warn("add customer rep but params can not validate.");
            throw new BizException(ErrorCode.CUSTOMER_REP_PARAMS_ERROR);
        }
        if (reqRep.getIsDefault() != null && reqRep.getIsDefault()) {
            reqRep.setDefaultTime(new Date()); //设置为默认使用
        }else {
            reqRep.setDefaultTime(null);
        }
        reqRep.setCreateTime(new Date());
        reqRep.setUpdateBy(reqRep.getCreateBy());
        reqRep.setUpdateTime(new Date());
        int count = customerRepMapper.insert(reqRep);
        if (count > 0) {
            return reqRep;
        }else {
            logger.warn("add customer rep but insert database fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    public CustomerRep updateRep(CustomerRep rep) throws BizException {
        if (rep == null || rep.getId() == null) {
            logger.warn("update customer rep but request rep id is null.");
            throw new BizException(ErrorCode.CUSTOMER_REP_PARAMS_ERROR);
        }
        CustomerRep reqRep = rep;
        if (!validateRep(reqRep)) {
            logger.warn("update customer rep but params can not validate.");
            throw new BizException(ErrorCode.CUSTOMER_REP_PARAMS_ERROR);
        }
        if (reqRep.getIsDefault()) {
            reqRep.setDefaultTime(new Date());
        }
        reqRep.setUpdateTime(new Date());
        int count = customerRepMapper.updateByPrimaryKeySelective(reqRep);
        if (count > 0) {
            return reqRep;
        }else {
            logger.warn("update customer rep but update database fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
    }

    private boolean validateRep(CustomerRep rep) throws BizException {
        if (rep == null) {
            return false;
        }
        if (rep.getCustomerId() == null) {
            logger.warn("validate rep but customer id is null.");
            return false;
        }
        if (rep.getName() == null) {
            logger.warn("validate rep but name is null.");
            return false;
        }
        if (rep.getContactPhone() == null) {
            logger.warn("validate rep but contact phone is null.");
            return false;
        }
        if (rep.getRepertoryAddress() == null) {
            logger.warn("validate rep but repertory address is null.");
            return false;
        }
        Customer customer = customerMapper.selectByPrimaryKey(rep.getCustomerId());
        if (customer == null || CustomerStatus.DELETE.name().equalsIgnoreCase(customer.getStatus())) {
            logger.warn("validate rep but get customer fail.");
            throw new BizException(ErrorCode.CUSTOMER_GET_FAIL);
        }
        return true;
    }

    public int removeRep(Integer repId) throws BizException {
        return customerRepMapper.deleteByPrimaryKey(repId);
    }


}
