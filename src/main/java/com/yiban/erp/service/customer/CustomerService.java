package com.yiban.erp.service.customer;

import com.yiban.erp.constant.CustomerStatus;
import com.yiban.erp.dao.CustomerCertMapper;
import com.yiban.erp.dao.CustomerMapper;
import com.yiban.erp.dao.CustomerRepMapper;
import com.yiban.erp.dao.FileInfoMapper;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.entities.CustomerCert;
import com.yiban.erp.entities.CustomerRep;
import com.yiban.erp.entities.FileInfo;
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
    private CustomerCertMapper customerCertMapper;
    @Autowired
    private CustomerRepMapper customerRepMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;

    public Customer getDetail(Integer companyId, Integer id) {
        Customer customer = customerMapper.getCustomerDetailById(companyId, id);
        if (customer != null) {
            //设置代表人的使用默认标识
            setRepDefaultFlag(customer.getCustomerReps());
        }
        return customer;
    }

    public Customer addCustomer(Customer reqCustomer) throws BizException {
        Customer addCustomer = UtilTool.trimString(reqCustomer);
        if (!validateCust(reqCustomer, true)) {
            logger.warn("add customer validate fail.");
            throw new BizException(ErrorCode.CUSTOMER_REQUIRE_PARAMS_ERROR);
        }
        addCustomer.setCreateTime(new Date());
        addCustomer.setUpdateBy(addCustomer.getCreateBy());
        addCustomer.setUpdateTime(new Date());
        int count = customerMapper.insertSelective(addCustomer);
        if (count > 0 && addCustomer.getId() > 0) {
            return addCustomer;
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    public Customer updateCustomer(Customer reqCustomer) throws BizException {
        Customer updCustomer = UtilTool.trimString(reqCustomer);
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
        if (customer.getCustomerNo() == null) {
            logger.warn("add validate customer customerNo is null.");
            return false;
        }
        if (customer.getName() == null) {
            logger.warn("add validate customer name is null.");
            return false;
        }
        //TODO 验证如果档案编编号存在，需要验证档案编号

        return true;
    }

    public List<CustomerCert> getCertList(Integer customerId) {
        if (customerId == null) {
            return Collections.emptyList();
        }
        return customerCertMapper.getByCustomerId(customerId);
    }


    public CustomerCert addCert(CustomerCert cert) throws BizException {
        CustomerCert reqCert = UtilTool.trimString(cert);
        if (!validateCert(reqCert)) {
            logger.warn("add customer cert then params is not validate.");
            throw new BizException(ErrorCode.CUSTOMER_CERT_PARAMS_ERROR);
        }
        reqCert.setCreateTime(new Date());
        reqCert.setUpdateBy(reqCert.getCreateBy());
        reqCert.setUpdateTime(new Date());
        int count = customerCertMapper.insertSelective(reqCert);
        if (count > 0) {
            return reqCert;
        }else {
            logger.warn("add customer cert insert fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    public CustomerCert updateCert(CustomerCert cert) throws BizException {
        if (cert == null || cert.getId() == null) {
            logger.warn("update cert but id is null.");
            throw new BizException(ErrorCode.CUSTOMER_CERT_PARAMS_ERROR);
        }
        CustomerCert reqCert = UtilTool.trimString(cert);
        if (!validateCert(reqCert)) {
            logger.warn("update cert but params is not validate.");
            throw new BizException(ErrorCode.CUSTOMER_CERT_PARAMS_ERROR);
        }
        reqCert.setUpdateTime(new Date());
        int count = customerCertMapper.updateByPrimaryKey(reqCert);
        if (count > 0) {
            return reqCert;
        }else {
            logger.warn("update cert update database fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
    }

    private boolean validateCert(CustomerCert cert) throws BizException {
        if (cert == null) {
            return false;
        }
        if (cert.getCustomerId() == null) {
            logger.warn("validate cert customer id is null.");
            return false;
        }
        if (cert.getLicenseName() == null) {
            logger.warn("validate cert license name is null.");
            return false;
        }
        if (cert.getLicenseExp() == null) {
            logger.warn("validate cert license expired date is null.");
            return false;
        }
        if (cert.getImageNo() == null) {
            logger.warn("validate cert image no is null.");
            return false;
        }
        //获取客户信息
        Customer customer = customerMapper.selectByPrimaryKey(cert.getCustomerId());
        if (customer == null || CustomerStatus.DELETE.name().equalsIgnoreCase(customer.getStatus())) {
            logger.warn("add customer cert but get customer fail. customerId:{}", cert.getCustomerId());
            throw new BizException(ErrorCode.CUSTOMER_GET_FAIL);
        }
        //验证档案编号
        FileInfo fileInfo = fileInfoMapper.getByFileNo(customer.getCompanyId(), cert.getImageNo());
        if (fileInfo == null) {
            logger.error("add customer cert but imageNo is error. imageNo:{}", cert.getImageNo());
            throw new BizException(ErrorCode.CUSTOMER_CERT_IMAGE_NO_ERROR);
        }
        return true;
    }

    public int removeCerts(List<Integer> idList) throws BizException {
        if (idList == null || idList.isEmpty()) {
            logger.warn("remove certs but id array is null.");
            throw new BizException(ErrorCode.CUSTOMER_CERT_REMOVE_PARAMS);
        }
        return customerCertMapper.removeByIds(idList);
    }

    public CustomerRep getDefaultCustomerRep(Integer customerId) {
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

    private void setRepDefaultFlag(List<CustomerRep> reps) {
        if (reps == null || reps.isEmpty()) {
            return;
        }
        //先按客户好分组
        Map<Integer, List<CustomerRep>> tempMap = reps.stream()
                .collect(Collectors.groupingBy(CustomerRep::getCustomerId));
        for (Map.Entry<Integer, List<CustomerRep>> entry : tempMap.entrySet()) {
            Integer customerId = entry.getKey();
            List<CustomerRep> list = entry.getValue();
            CustomerRep defaultValue = getDefaultCustomerRep(customerId);
            if (defaultValue == null) {
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

    public List<CustomerRep> getRepList(Integer customerId) throws BizException {
        if (customerId == null) {
            return Collections.emptyList();
        }
        List<CustomerRep> reps = customerRepMapper.getByCustomerId(customerId);
        //设置默认标识
        setRepDefaultFlag(reps);
        return reps;
    }

    public CustomerRep addRep(CustomerRep rep) throws BizException {
        CustomerRep reqRep = UtilTool.trimString(rep);
        if (!validateRep(reqRep)) {
            logger.warn("add customer rep but params can not validate.");
            throw new BizException(ErrorCode.CUSTOMER_REP_PARAMS_ERROR);
        }
        if (reqRep.getIsDefault() == true) {
            reqRep.setDefaultTime(new Date()); //设置为默认使用
        }
        reqRep.setCreateTime(new Date());
        reqRep.setUpdateBy(reqRep.getCreateBy());
        reqRep.setUpdateTime(new Date());
        int count = customerRepMapper.insertSelective(reqRep);
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
        CustomerRep reqRep = UtilTool.trimString(rep);
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

    public int removeReps(List<Integer> idList) throws BizException {
        if (idList == null || idList.isEmpty()) {
            logger.warn("remove reps but id array is null.");
            throw new BizException(ErrorCode.CUSTOMER_REP_REMOVE_PARAMS);
        }
        return customerRepMapper.removeByIds(idList);
    }


}
