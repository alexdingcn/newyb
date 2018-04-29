package com.yiban.erp.service.financial;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.*;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.FinancialReq;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.util.LockService;
import com.yiban.erp.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 财务
 */
@Service
public class FinancialService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialService.class);

    private static final String FINANCIAL_KEY_PRE = "FINANCIAL-";

    @Autowired
    private FinancialFlowMapper financialFlowMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private LockService lockService;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private FinancialPrePaidMapper financialPrePaidMapper;


    /**
     * 根据销售单创建一笔往来账流水记录
     * @param order
     */
    public void createFlowBySellOrder(SellOrder order) throws BizException {
        if (order == null || !SellOrderStatus.SALE_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("sell order is null or sell order status is not SALE_CHECKED");
            throw new BizException(ErrorCode.FINANCIAL_SELL_ORDER_ERROR);
        }
        //验证下当前销售单是否已经记录过往来账，如果记录过，给出错误提示
        List<FinancialFlow> flows = financialFlowMapper.getByRefId(order.getId());
        if (flows != null && !flows.isEmpty()) {
            logger.warn("sell order:{} financial flow is already exist, can not add on this sell order. ", order.getId());
            throw new BizException(ErrorCode.FINANCIAL_SELL_ORDER_EXIST);
        }

        FinancialReq req = new FinancialReq();
        req.setCompanyId(order.getCompanyId());
        req.setLogUserName(order.getUpdateBy());
        req.setCustType(FinancialReq.CUST_CUSTOMER);
        req.setCustId(order.getCustomerId());
        req.setBizType(FinancialBizType.SELL_BATCH.name());
        req.setBizRefId(order.getId()); //关联单ID取采购订单的ID
        req.setBizRefNo(order.getOrderNumber());
        req.setLogAmount(order.getTotalAmount());
        req.setLogDate(new Date());
        req.setLogAccount(order.getCustomerName());
        req.setKeyWord("销售单生成的往来账流水"); //入库方式的描述信息

        doFinancialRecord(req);  //登记往来账逻辑
    }

    /**
     * 采购入库，根据采购的入库单，进行建立对应的财务往来账
     * @param repertoryIn
     */
    public void createFlowByBuyOrder(RepertoryIn repertoryIn) throws BizException {
        //只针对两种类型的入库进行统计对应的财务往来账
        if (repertoryIn == null || !RepertoryInStatus.IN_CHECKED.name().equalsIgnoreCase(repertoryIn.getStatus())) {
            logger.warn("repertory in order record financial record but order is null or status is not IN_CHECKED");
            throw new BizException(ErrorCode.FINANCIAL_IN_ORDER_ERROR);
        }
        if (!RepertoryRefType.BUY_ORDER.name().equalsIgnoreCase(repertoryIn.getRefType()) &&
                !RepertoryRefType.BUY_DIRECT.name().equalsIgnoreCase(repertoryIn.getRefType())) {
            //当前方法只能统计采购单入库和直调入库的入库单信息
            logger.warn("repertory in order ref type is not BUY_ORDER or BUY_DIRECT. orderId:{}", repertoryIn.getId());
            throw new BizException(ErrorCode.FINANCIAL_IN_ORDER_ERROR);
        }
        //验证下当前销售单是否已经记录过往来账，如果记录过，给出错误提示
        List<FinancialFlow> flows = financialFlowMapper.getByRefId(repertoryIn.getId());
        if (flows != null && !flows.isEmpty()) {
            logger.warn("repertory in order:{} financial flow is already exist, can not add on this order.", repertoryIn.getId());
            throw new BizException(ErrorCode.FINANCIAL_SELL_ORDER_EXIST);
        }
        FinancialReq req = new FinancialReq();
        req.setCompanyId(repertoryIn.getCompanyId());
        req.setLogUserName(repertoryIn.getUpdateBy());
        req.setCustType(FinancialReq.CUST_SUPPLIER);
        req.setCustId(repertoryIn.getSupplierId());
        req.setBizType(FinancialBizType.BUY_IN.name());
        req.setBizRefId(repertoryIn.getId()); //关联单ID取入库单的ID
        req.setBizRefNo(repertoryIn.getOrderNumber());
        req.setLogAmount(repertoryIn.getTotalAmount());
        req.setLogDate(new Date());
        req.setLogAccount(repertoryIn.getSupplierName());
        req.setKeyWord(repertoryIn.getRefTypeName()); //入库方式的描述信息

        doFinancialRecord(req);  //登记往来账逻辑
    }

    /**
     * 取消操作的入口，根据被取消的流水信息组织参数
     */
    public void flowCancel(FinancialFlow flow, User user) throws BizException {
        FinancialFlow refFlow = financialFlowMapper.selectByPrimaryKey(flow.getId());
        if (refFlow == null || !refFlow.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("user:{} request cancel flow:{} get fail.", user.getId(), flow.getId());
            throw new BizException(ErrorCode.FINANCIAL_GET_FAIL);
        }
        //验证下当前关联单的类型是否可以做取消操作
        FinancialBizType refBizType = FinancialBizType.getByName(refFlow.getBizType());
        if (refBizType == null || !refBizType.isCanCancel()) {
            logger.warn("bizType:{} can not do cancel action. refId:{}", refFlow.getBizType(), refFlow.getId());
            throw new BizException(ErrorCode.FINANCIAL_CANNOT_CANCEL);
        }
        FinancialBizType currCancelBizType = FinancialBizType.getCancelBizTypeByRefBizType(refBizType);
        if (currCancelBizType == null) {
            logger.warn("get cancel bizType fail by refBizType:{}", refBizType.name());
            throw new BizException(ErrorCode.FINANCIAL_CANNOT_CANCEL);
        }
        List<FinancialFlow> flows = financialFlowMapper.getByRefId(refFlow.getId());
        for (FinancialFlow item : flows) {
            if (item.getBizType().equalsIgnoreCase(currCancelBizType.name())) {
                logger.warn("当前流水已经做过取消操作，不能再次做取消操作。flowId:{}, itemId:{}", flow.getId(), item.getId());
                throw new BizException(ErrorCode.FINANCIAL_CANNOT_CANCEL_AGAIN);
            }
        }
        //验证通过后，组织请求参数操作入库
        FinancialReq req = new FinancialReq();
        req.setCompanyId(refFlow.getCompanyId());
        req.setLogUserName(user.getNickname());
        req.setCustType(refFlow.getCustType());
        req.setCustId(refFlow.getCustId());
        req.setBizType(currCancelBizType.name());
        req.setBizRefId(refFlow.getId());
        req.setBizRefNo(refFlow.getBizNo());
        req.setLogAmount(refFlow.getLogAmount());
        req.setLogDate(new Date());
        req.setLogAccount(refFlow.getLogAccount());
        req.setCompanyAccount(refFlow.getCompanyAccount());
        req.setCustAccount(refFlow.getCustAccount());
        req.setDocNo(flow.getDocNo());
        req.setFileNo(flow.getFileNo());
        req.setKeyWord(flow.getKeyWord());
        logger.info("do cancel insert request params: {}", JSON.toJSONString(req));
        doFinancialRecord(req);
    }

    /**
     * 针对的是操作员的操作
     * 只针对：付款、收款、记账应收、记账应付着几种操作。
     * @param financialReq
     * @throws BizException
     */
    public void payAndReceiveFinancialRecord(FinancialReq financialReq) throws BizException{
        if (financialReq == null) {
            logger.warn("request params is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        FinancialBizType bizType = FinancialBizType.getByName(financialReq.getBizType());
        if (bizType == null) {
            logger.warn("request params bizType is error.");
            throw new BizException(ErrorCode.FINANCIAL_BIZ_TYPE_ERROR);
        }
        //根据各种操作的不同，验证通过进行各种验证后进行登记往来账流水
        switch (bizType) {
            case RECEIVE:
            case PAY:
            case RECORD_RECEIVE:
            case RECORD_PAY:
                doFinancialRecord(financialReq);
                break;
            default:
                // 其他的操作目前不支持使用该方法
                throw new BizRuntimeException(ErrorCode.FINANCIAL_ACTION_ERROR);
        }
    }

    private boolean validateFinancialReq(FinancialReq req) {
        if (req == null) {
            return false;
        }
        if (req.getCompanyId() == null) {
            logger.warn("request params companyId is null.");
            return false;
        }
        if (req.getLogUserName() == null) {
            logger.warn("request params logUserName is null");
            return false;
        }
        if (req.getCustId() == null) {
            logger.warn("request params custId is null.");
            return false;
        }
        if (!FinancialReq.CUST_CUSTOMER.equalsIgnoreCase(req.getCustType())
                && !FinancialReq.CUST_SUPPLIER.equalsIgnoreCase(req.getCustType())) {
            logger.warn("request params custType is error, custType:{}", req.getCustType());
            return false;
        }
        if (req.getLogDate() == null) {
            logger.warn("request params logDate is null.");
            return false;
        }
        if (req.getLogAmount() == null || BigDecimal.ZERO.compareTo(req.getLogAmount()) >= 0) {
            logger.warn("request params logAmount is null or logAmount <= 0");
            return false;
        }
        if (req.getLogAccount() == null) {
            logger.warn("request params logAccount is null");
            return false;
        }
        FinancialBizType type = FinancialBizType.getByName(req.getBizType());
        if (type == null) {
            logger.warn("request params biz type is error. {}", req.getBizType());
            return false;
        }
        return true;
    }

    private FinancialFlow doFinancialRecord(FinancialReq financialReq) throws BizException{
        if (!validateFinancialReq(financialReq)) {
            logger.warn("financial request params validate fail. {}", JSON.toJSONString(financialReq));
            throw new BizRuntimeException(ErrorCode.PARAMETER_MISSING);
        }
        String lockKey = FINANCIAL_KEY_PRE + financialReq.getCompanyId();
        boolean lock = lockService.lock(lockKey);
        if (lock) {
            try {
                return addNewFinancialRecord(financialReq);
            } catch (Exception e){
                logger.error("do financial record have exception. {}", JSON.toJSONString(financialReq), e);
                // TODO 发送警告邮件
                throw e; //往上层抛出
            }finally {
                lockService.closeLock(lockKey);
            }
        }else {
            logger.error("get financial lock fail by key:{}", lockKey);
            throw new BizException(ErrorCode.FINANCIAL_GET_LOCK_FAIL);
            // TODO 发送警告邮件信息
        }
    }

    /**
     * 直接操作新建逻辑，必须在改操作之前做好加锁操作
     * 改方法虽然是public 的方法，主要是用户回滚事物的，不要直接调用，除非所有数据都已经验证通过
     * @param financialReq
     */
    @Transactional
    public FinancialFlow addNewFinancialRecord(FinancialReq financialReq) {
        Company company = companyMapper.selectByPrimaryKey(financialReq.getCompanyId());
        if (company == null) {
            logger.error("get company by companyId:{} result is null.",financialReq.getCompanyId());
            throw new BizRuntimeException(ErrorCode.COMPANY_MISS);
        }
        Customer customer = null;
        Supplier supplier = null;
        // 如果记账的客户类型是客户，则获取客户信息, 如果是供应商，获取供应商的信息
        if (FinancialReq.CUST_SUPPLIER.equalsIgnoreCase(financialReq.getCustType())) {
            supplier = supplierMapper.selectByPrimaryKey(financialReq.getCustId());
            if (supplier == null) {
                throw new BizRuntimeException(ErrorCode.CUSTOMER_GET_FAIL);
            }
            financialReq.setLogAccount(supplier.getName()); //直接设置为供应商名称
        }else if (FinancialReq.CUST_CUSTOMER.equalsIgnoreCase(financialReq.getCustType())) {
            customer = customerMapper.selectByPrimaryKey(financialReq.getCustId());
            if (customer == null) {
                throw new BizRuntimeException(ErrorCode.CUSTOMER_GET_FAIL);
            }
            financialReq.setLogAccount(customer.getName()); //直接设置为客户名称
        }

        BigDecimal logAmount = financialReq.getLogAmount() == null ? BigDecimal.ZERO : financialReq.getLogAmount();
        FinancialBizType bizType = FinancialBizType.getByName(financialReq.getBizType());
        BigDecimal inAmount = getActionAmount(bizType, logAmount, 1);
        BigDecimal outAmount = getActionAmount(bizType, logAmount, -1);

        //公司的账户余额：公司账户余额 = 原公司账户余额 + 应收账款 - 应付账款
        //公司的应收余额：公司应收余额 = 原始应收余额 + 应收账款
        //公司的应付余额：公司应付余额 = 原始应付余额 + 应付账款
        BigDecimal companyAccountAmount = company.getAccountAmount() == null ? BigDecimal.ZERO : company.getAccountAmount();
        BigDecimal companyInAmount = company.getInAmount() == null ? BigDecimal.ZERO : company.getInAmount();
        BigDecimal companyOutAmount = company.getOutAmount() == null ? BigDecimal.ZERO : company.getOutAmount();
        BigDecimal companyNewAmount = companyAccountAmount.add(inAmount == null ? BigDecimal.ZERO : inAmount).subtract(outAmount == null ? BigDecimal.ZERO : outAmount);
        BigDecimal companyNewInAmount = companyInAmount.add(inAmount == null ? BigDecimal.ZERO : inAmount);
        BigDecimal companyNewOutAmount = companyOutAmount.add(outAmount == null ? BigDecimal.ZERO : outAmount);
        //修改公司账户的余额
        int companyUpdate = companyMapper.updateAccountAmount(company.getId(), companyNewAmount, companyNewInAmount, companyNewOutAmount);
        if (companyUpdate <= 0) {
            logger.warn("update company account amount fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        BigDecimal custAmount = null;
        if (FinancialReq.CUST_SUPPLIER.equalsIgnoreCase(financialReq.getCustType())) {
            //客户的账户余额：客户账户余额 = 原始客户账户余额 + 应付账款 - 应收账款
            BigDecimal supplierAmount = supplier.getAccountAmount() == null ? BigDecimal.ZERO : supplier.getAccountAmount();
            custAmount = supplierAmount.add(outAmount == null ? BigDecimal.ZERO : outAmount).subtract(inAmount == null ? BigDecimal.ZERO : inAmount);
            int supplierUpdate = supplierMapper.updateAccountAmount(supplier.getId(), custAmount);
            if (supplierUpdate <= 0) {
                logger.warn("update supplier account amount fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }else if (FinancialReq.CUST_CUSTOMER.equalsIgnoreCase(financialReq.getCustType())) {
            //客户的账户余额：客户账户余额 = 原始客户账户余额 + 应付账款 - 应收账款
            BigDecimal customerAmount = customer.getAccountAmount() == null ? BigDecimal.ZERO : customer.getAccountAmount();
            custAmount = customerAmount.add(outAmount == null ? BigDecimal.ZERO : outAmount).subtract(inAmount == null ? BigDecimal.ZERO : inAmount);
            int customerUpdate = customerMapper.updateAccountAmount(customer.getId(), custAmount);
            if (customerUpdate <= 0) {
                logger.warn("update customer account amount fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }
        //登记往来账记录
        FinancialFlow flow = new FinancialFlow();
        flow.setCompanyId(financialReq.getCompanyId());
        flow.setCustType(financialReq.getCustType());
        flow.setCustId(financialReq.getCustId());
        flow.setBizNo(UtilTool.makeOrderNumber(company.getId(), OrderNumberType.FINANCIAL_FLOW));
        flow.setLogDate(financialReq.getLogDate());
        flow.setLogAccount(financialReq.getLogAccount());
        flow.setDocNo(financialReq.getDocNo());
        flow.setFileNo(financialReq.getFileNo());
        flow.setBizType(financialReq.getBizType());
        flow.setBizRefId(financialReq.getBizRefId());
        flow.setBizRefNo(financialReq.getBizRefNo());
        flow.setKeyWord(financialReq.getKeyWord() == null ? bizType.getDesc() : financialReq.getKeyWord());
        flow.setLogAmount(financialReq.getLogAmount());
        flow.setInAmount(inAmount);
        flow.setOutAmount(outAmount);
        flow.setSurplusInAmount(companyNewInAmount); //公司的应收账款余额
        flow.setSurplusOutAmount(companyNewOutAmount); //公司的应付账款余额
        flow.setCompanyAmount(companyNewAmount); //公司账户余额(大于0代表盈利)
        flow.setCustAmount(custAmount); //客户账户余额(大于0代表客户欠公司的钱，小于0代表公司欠客户钱)
        flow.setPayMethod(financialReq.getPayMethod());
        flow.setCompanyAccount(financialReq.getCompanyAccount());
        flow.setCustAccount(financialReq.getCustAccount());
        flow.setCreatedBy(financialReq.logUserName);
        flow.setCreatedTime(new Date());
        int financialCount = financialFlowMapper.insert(flow);
        if (financialCount <= 0) {
            logger.warn("insert financial fail. {}", JSON.toJSONString(flow));
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        return flow;
    }

    private BigDecimal getActionAmount(FinancialBizType bizType, BigDecimal logAmount, int inOrOutType) {
        BigDecimal inAmount = null; //应收
        BigDecimal outAmount = null; //应付
        switch (bizType) {
            case BUY_IN:
                //采购入库：需要付钱给客户，所以应付增加, 应收不变
                outAmount = logAmount;
                break;
            case BUY_BACK:
                //采购退货：需要收回付给客户的钱, 所以应收增加，应付不变
                inAmount = logAmount;
                break;
            case SELL_BACK:
                //销售退货：需要付给客户多收的钱, 所以应付增加，应收不变
                outAmount = logAmount;
                break;
            case SELL_BATCH:
                //销售出库：需要收客户的钱, 所以应收增加，应付不变
                inAmount = logAmount;
                break;
            case RECEIVE:
                //收款：应该收客户的钱减少，所以应收减少，应付不变
                inAmount = logAmount.negate(); //取反为负数，发生额(logAmount)都为大于0的数
                break;
            case RECEIVE_CANCEL:
                //收款取消：应该收客户的钱加回来，所以应收增加，应付不变
                inAmount = logAmount;
                break;
            case PAY:
                //付款：应该付给客户的钱减少, 所以应付减少，应收不变
                outAmount = logAmount.negate();
                break;
            case PAY_CANCEL:
                //付款取消：应该付给客户的钱需要加回来，所以应付增加，应收不变
                outAmount = logAmount;
                break;
            case PRE_RECEIVE:
                //预收款：预先收客户的钱，相当于多了一笔应付款，借钱给客户的意思，应收增加，应付不变
                inAmount = logAmount;
                break;
            case PRE_RECEIVE_CANCEL:
                //预收款取消：预先收款取消，需要把预收款的应收取消，应收减少，应付不变
                inAmount = logAmount.negate();
                break;
            case PRE_PAID:
                //预付款：预先付款给客户，相当于多了一笔应收款，应收增加，应付不变
                inAmount = logAmount;
                break;
            case PRE_PAID_CANCEL:
                //预付款取消：预先付款取消，相当于需要把应收款取消，应收减少，应付不变
                inAmount = logAmount.negate();
                break;
            case RECORD_PAY:
                //记账应付：记录一笔应该付给客户多少钱，直接记在应付上，应付增加，应收不变
                outAmount = logAmount;
                break;
            case RECORD_PAID_CANCEL:
                //记账应付取消：需要把记录的一笔应付取消，应付减少，应收不变
                outAmount = logAmount.negate();
                break;
            case RECORD_RECEIVE:
                //记账应收：记录一笔应该收客户多少钱，直接记录在应收上，应收增加，应付不变
                inAmount = logAmount;
                break;
            case RECORD_RECEIVE_CANCEL:
                //记账应收取消：记录的应收取消，所以应收减少，应付不变
                inAmount = logAmount.negate();
                break;
            case OFFSET:
                //冲销操作: 有两种冲销，冲销只能针对预收款和预付款;
                //预收款冲销：相当于原来应该收的钱减少了，应该付的钱拿回来了(减少)
                //预付款冲销：相当于原来应该付的钱减少了，应该收的钱拿回来了(减少)
                inAmount = logAmount.negate();
                outAmount = logAmount.negate();
                break;
            default:
                throw new BizRuntimeException(ErrorCode.FINANCIAL_BIZ_TYPE_ERROR);
        }
        return inOrOutType > 0 ? inAmount : outAmount;  // 大于0表示去应收金额，否则去应付金额
    }

    @Transactional
    public FinancialFlow flowUpdate(FinancialFlow updateFlow, User user) throws BizException {
        FinancialFlow flow = financialFlowMapper.getIncludeOptionById(updateFlow.getId());
        if (flow == null || !flow.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get need update flow fail. id:{}", updateFlow.getId());
            throw new BizException(ErrorCode.FINANCIAL_GET_FAIL);
        }
        //只修改部分信息，其他信息不修改
        flow.setKeyWord(updateFlow.getKeyWord());
        flow.setDocNo(updateFlow.getDocNo());
        flow.setFileNo(updateFlow.getFileNo());
        flow.setCustAccount(updateFlow.getCustAccount());
        flow.setCompanyAccount(updateFlow.getCompanyAccount());
        flow.setKeyWord(updateFlow.getKeyWord());
        flow.setUpdatedBy(user.getNickname());
        flow.setUpdatedTime(new Date());
        int count = financialFlowMapper.updateByPrimaryKeySelective(flow);
        if (count > 0) {
            return flow;
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }

    private void validateAddPrePaid(FinancialPrePaid prePaid) throws BizException {
        if (prePaid == null || prePaid.getCompanyId() == null) {
            logger.warn("params is null or companyId is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (prePaid.getLogUserId() == null || prePaid.getCreatedBy() == null) {
            logger.warn("params do user is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (prePaid.getLogAmount() == null || BigDecimal.ZERO.compareTo(prePaid.getLogAmount()) >= 0) {
            logger.warn("params log amount must > 0");
            throw new BizException(ErrorCode.FINANCIAL_AMOUNT_ERROR);
        }
        if (prePaid.getCustId() == null || prePaid.getCustName() == null) {
            logger.warn("params cust id is null.");
            throw new BizException(ErrorCode.FINANCIAL_CUST_ID_NULL);
        }
        return;
    }

    @Transactional
    public FinancialPrePaid addPrePaid(FinancialPrePaid prePaid) throws BizException {
        //验证信息
        validateAddPrePaid(prePaid); // 报错就验证不过
        Supplier supplier = supplierMapper.selectByPrimaryKey(prePaid.getCustId());
        if (supplier == null || supplier.getEnabled() == null || !supplier.getEnabled()) {
            logger.warn("pre paid but supplier get fail or enabled is false. supplierId:{}", prePaid.getCustId());
            throw new BizException(ErrorCode.FINANCIAL_CUST_GET_FAIL);
        }
        //组织往来账流水请求body
        FinancialReq financialReq = new FinancialReq();
        financialReq.setCompanyId(prePaid.getCompanyId());
        financialReq.setLogUserName(prePaid.getCreatedBy());
        financialReq.setCustType(FinancialReq.CUST_SUPPLIER);
        financialReq.setCustId(prePaid.getCustId());
        financialReq.setBizType(FinancialBizType.PRE_PAID.name());
        financialReq.setLogAmount(prePaid.getLogAmount());
        financialReq.setLogDate(prePaid.getLogDate());
        financialReq.setLogAccount(prePaid.getCustName());
        financialReq.setCustAccount(prePaid.getCustAccount());
        financialReq.setDocNo(prePaid.getDocNo());
        financialReq.setFileNo(prePaid.getFileNo());
        financialReq.setKeyWord(prePaid.getKeyWord());
        logger.info("do pre paid financial flow request params: {}", JSON.toJSONString(financialReq));
        FinancialFlow flow = doFinancialRecord(financialReq);
        if (flow == null || flow.getId() == null) {
            logger.warn("add financial flow fail in pre paid request.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        //流水号建立好后,建立对应的预收款记录
        prePaid.setFlowId(flow.getId());
        prePaid.setBizNo(flow.getBizNo());
        prePaid.setStatus(FinancialPreStatus.INIT.name());
        prePaid.setCreatedTime(new Date());
        financialPrePaidMapper.insert(prePaid);
        return prePaid;
    }

}
