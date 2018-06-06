package com.yiban.erp.service.customer;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.CustomerStatus;
import com.yiban.erp.constant.FinancialBizType;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.CustomerMapper;
import com.yiban.erp.dao.CustomerRepMapper;
import com.yiban.erp.dao.FinancialFlowMapper;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.dto.FinancialQuery;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.entities.CustomerRep;
import com.yiban.erp.entities.FinancialFlow;
import com.yiban.erp.entities.StatusCount;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerRepMapper customerRepMapper;

    @Autowired
    private SellOrderMapper sellOrderMapper;

    @Autowired
    private FinancialFlowMapper financialFlowMapper;

    public Customer getDetail(Integer companyId, Long id) {
        Customer customer = customerMapper.getCustomerDetailById(companyId, id);
        if (customer != null) {
            //设置代表人的使用默认标识
            setRepDefaultFlag(customer.getCustomerReps());
        }
        return customer;
    }

    public JSONObject getCustomerStats(Integer companyId, Long customerId) {
        List<StatusCount> totalStats = sellOrderMapper.getCustomerStat(companyId, customerId, null, null);
        Date firstDay = DateUtils.truncate(new Date(), Calendar.MONTH);
        List<StatusCount> thisMonthStats = sellOrderMapper.getCustomerStat(companyId, customerId, firstDay, new Date());


        JSONObject result = new JSONObject();
        if ( totalStats != null && totalStats.size() > 0) {
            StatusCount totalStat = totalStats.get(0);
            result.put("totalOrderCount", totalStat.getOrderCount());
            result.put("totalOrderAmount", totalStat.getAmount());
            // 订货频率
            result.put("avgOrderGap", totalStat.getAvgOrderGap());
            // 客单价
            result.put("avgOrderAmount", totalStat.getAvgOrderAmount());
            result.put("totalReceivable", totalStat.getCustomerReceivable());
            result.put("latestOrderDate", totalStat.getLatestOrderDate());
        }
        if ( thisMonthStats != null && thisMonthStats.size() > 0) {
            StatusCount monthStat = thisMonthStats.get(0);
            //本月成交额
            result.put("monthOrderAmount", monthStat.getAmount());

            FinancialQuery query = new FinancialQuery();
            query.setCustId(customerId);
            query.setCompanyId(companyId);
            query.setLogStartDate(firstDay);
            List<FinancialFlow> flows = financialFlowMapper.getFlowList(query);
            BigDecimal received = BigDecimal.ZERO;
            BigDecimal receivable = BigDecimal.ZERO;
            if (flows != null) {
                for (FinancialFlow flow: flows) {
                    // 本月收款= 收款+预收-收款取消-预收取消
                    if (FinancialBizType.RECEIVE.name().equals(flow.getBizType()) || FinancialBizType.PRE_RECEIVE.name().equals(flow.getBizType())) {
                        received = received.add(flow.getLogAmount());
                    }
                    else if (FinancialBizType.RECEIVE_CANCEL.name().equals(flow.getBizType()) || FinancialBizType.PRE_RECEIVE_CANCEL.name().equals(flow.getBizType())) {
                        received = received.subtract(flow.getLogAmount());
                    }

                    // 本月应收= 销售应收+手工应收 - 应收取消
                    if (FinancialBizType.RECORD_RECEIVE.name().equals(flow.getBizType()) || FinancialBizType.SELL_BATCH.name().equals(flow.getBizType())) {
                        receivable = receivable.add(flow.getLogAmount());
                    }
                    else if (FinancialBizType.RECORD_RECEIVE_CANCEL.name().equals(flow.getBizType())) {
                        receivable = receivable.subtract(flow.getLogAmount());
                    }
                }
            }
            DecimalFormat formatter = new DecimalFormat("#0.00");
            //  本月新增应收款：
            result.put("monthReceivable", formatter.format(receivable));
            //  本月已回款：
            result.put("monthReceived", formatter.format(received));
        }

        return result;
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
        } else {
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
        } else {
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
        } else {
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
                } else {
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
        } else {
            reqRep.setDefaultTime(null);
        }
        reqRep.setCreateTime(new Date());
        reqRep.setUpdateBy(reqRep.getCreateBy());
        reqRep.setUpdateTime(new Date());
        int count = customerRepMapper.insert(reqRep);
        if (count > 0) {
            return reqRep;
        } else {
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
        } else {
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
