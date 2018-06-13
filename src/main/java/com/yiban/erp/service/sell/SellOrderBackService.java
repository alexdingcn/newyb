package com.yiban.erp.service.sell;

import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.constant.*;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.dao.SellOrderBackDetailMapper;
import com.yiban.erp.dao.SellOrderBackMapper;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.dto.SellBackCheck;
import com.yiban.erp.dto.SellBackQuery;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.goods.GoodsService;
import com.yiban.erp.service.util.SystemConfigService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.ORB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SellOrderBackService {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderBackService.class);

    @Autowired
    private SellOrderBackMapper sellOrderBackMapper;
    @Autowired
    private SellOrderBackDetailMapper sellOrderBackDetailMapper;
    @Autowired
    private SellOrderMapper sellOrderMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;
    @Autowired
    private SystemConfigService systemConfigService;


    private void validateAddOrder(SellOrderBack back) throws BizException {
        if (back == null || back.getRefOrderId() == null) {
            logger.warn("params refOrderId is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (back.getCustomerId() == null) {
            logger.warn("params customerId is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (back.getSaleId() == null) {
            logger.warn("params saleId is null");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (back.getWarehouseId() == null) {
            logger.warn("params warehouse id is null");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (back.getFreeAmount() != null && BigDecimal.ZERO.compareTo(back.getFreeAmount()) > 0) {
            logger.warn("params costAmount must <= 0");
            throw new BizException(ErrorCode.SELL_BACK_COST_AMOUNT_ERROR);
        }
        List<SellOrderBackDetail> details = back.getDetails();
        if (details == null || details.isEmpty()) {
            logger.warn("params details is empty.");
            throw new BizException(ErrorCode.SELL_BACK_ADD_DETAIL_EMPTY);
        }
    }

    public void applySave(SellOrderBack orderBack, User user) throws BizException {
        validateAddOrder(orderBack);
        //获取详情退货数量大于0的产品信息
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(orderBack.getRefOrderId());
        if (sellOrder == null || !sellOrder.getCompanyId().equals(user.getCompanyId())
                || !SellOrderStatus.SALE_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())) {
            throw new BizException(ErrorCode.SELL_BACK_GET_OUT_RECORD_FAIL);
        }

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCostAmount = BigDecimal.ZERO;
        for (SellOrderBackDetail detail : orderBack.getDetails()) {
            totalQuantity = totalQuantity.add(detail.getBackQuantity());
            totalAmount = totalAmount.add(detail.getAmount() == null ? BigDecimal.ZERO : detail.getAmount());
            totalCostAmount = totalCostAmount.add(detail.getCostAmount() == null ? BigDecimal.ZERO : detail.getCostAmount());
        }
        orderBack.setTotalQuantity(totalQuantity);
        orderBack.setTotalAmount(totalAmount);
        orderBack.setTotalCostAmount(totalCostAmount);
        if (orderBack.getId() == null) {
            //新建退货申请单
            createSellBackOrder(orderBack, user);
        }else {
            //检查是否正确
            SellOrderBack old = sellOrderBackMapper.selectByPrimaryKey(orderBack.getId());
            if (old == null || !old.getCompanyId().equals(user.getCompanyId())) {
                logger.warn("get sellOrderBack record fail by id:{}", orderBack.getId());
                throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
            }
            //只有在未收货之前能进行修改，否则不能进行修改
            if (!(SellBackStatus.INIT.name().equalsIgnoreCase(old.getStatus())
                    || SellBackStatus.INIT_SALE_CHECKED.name().equalsIgnoreCase(old.getStatus())
                    || SellBackStatus.INIT_QUALITY_CHECKED.name().equalsIgnoreCase(old.getStatus()))) {
                logger.info("update sell back order status is :{} can not do update. id:{}", old.getStatus(), old.getId());
                throw new BizException(ErrorCode.SELL_BACK_ORDER_CANNOT_UPDATE);
            }
            //修改退货申请单
            updateSellBackOrder(orderBack, user);
        }
        //修改订单详情的退货数量
        sellOrderBackDetailMapper.updateAlreadyBackQuantity(orderBack.getRefOrderId());
    }

    @Transactional
    public void createSellBackOrder(SellOrderBack back, User user) {
        //直接把记录信息保存入库
        back.setCompanyId(user.getCompanyId());
        back.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.SELL_BACK));
        back.setStatus(SellBackStatus.INIT.name());
        back.setCreatedBy(user.getNickname());
        back.setCreatedTime(new Date());
        int count = sellOrderBackMapper.insert(back);
        if (count <=0 || back.getId() == null) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }

        //详情
        for (SellOrderBackDetail detail : back.getDetails()) {
            detail.setCreatedTime(new Date());
            detail.setCreatedBy(user.getNickname());
            detail.setBackOrderId(back.getId());
            detail.setRightQuantity(detail.getBackQuantity()); //初始设置为全部正确
            detail.setBadQuantity(BigDecimal.ZERO);
        }
        int detailCount = sellOrderBackDetailMapper.insertBatch(back.getDetails());
        if (detailCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("sell back order detail insert success. detailCount:{}", detailCount);
    }

    @Transactional
    public void updateSellBackOrder(SellOrderBack back,  User user) {
        back.setStatus(SellBackStatus.INIT.name());
        back.setUpdatedBy(user.getNickname());
        back.setUpdatedTime(new Date());
        int count = sellOrderBackMapper.updateByPrimaryKeySelective(back);
        if (count <= 0 ) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }

        //详情，先全部删除原来的，然后再次重建
        sellOrderBackDetailMapper.deleteByBackOrderId(back.getId());

        //详情
        for (SellOrderBackDetail detail : back.getDetails()) {
            detail.setCreatedTime(new Date());
            detail.setCreatedBy(user.getNickname());
            detail.setBackOrderId(back.getId());
            detail.setRightQuantity(detail.getBackQuantity()); //初始设置为全部正确
            detail.setBadQuantity(BigDecimal.ZERO);
        }
        int detailCount = sellOrderBackDetailMapper.insertBatch(back.getDetails());
        if (detailCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("sell back order detail insert success. detailCount:{}", detailCount);
    }


    public List<SellOrderBack> getOrderList(SellBackQuery query) {
        if (query == null || query.getCompanyId() == null) {
            return Collections.emptyList();
        }
        return sellOrderBackMapper.getOrderList(query);
    }

    public List<SellOrderBackDetail> getDetails(Long backOrderId) {
        List<SellOrderBackDetail> details = sellOrderBackDetailMapper.getByBackOrderId(backOrderId);
        if (details.isEmpty()) {
            return details;
        }
        List<Long> goodsIds = new ArrayList<>();
        details.stream().forEach(item -> goodsIds.add(item.getGoodsId()));
        List<Goods> goodsList = goodsService.getGoodsById(goodsIds);
        Map<Long, Goods> goodsMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodsMap.put(item.getId(), item));
        details.stream().forEach(item -> item.setGoods(goodsMap.get(item.getGoodsId())));
        return details;
    }

    /**
     * 各种流程审核入口审核
     * @param backCheck
     * @param user
     * @return 1: 需要刷新订单列表，0: 只需要刷新详情列表
     * @throws BizException
     */
    public int checkBackOrder(SellBackCheck backCheck, User user) throws BizException {
        //检查验请求的状态是否正确
        SellBackStatus requestStatus = SellBackStatus.getByName(backCheck.getStatus());
        if (requestStatus == null) {
            logger.warn("get sell back status fail by status:{}", backCheck.getStatus());
            throw new BizException(ErrorCode.SELL_BACK_CHECK_STATUS_ERROR);
        }
        switch (requestStatus) {
            case INIT_SALE_CHECKED:
                initSaleCheck(backCheck, user);//初始销售经理审核
                return 1;
            case INIT_QUALITY_CHECKED:
                initQualityCheck(backCheck, user);//初始质量经理审核
                return 1;
            case QUALITY_CHECKED:
                return qualityCheck(backCheck, user);
            case FINAL_CHECKED:
                finalCheck(backCheck, user);
                return 1;
        }
        logger.warn("get request status error. requestStatus:{}", requestStatus.name());
        throw new BizException(ErrorCode.SELL_BACK_CHECK_STATUS_ERROR);
    }

    /**
     * 销售经理初审
     * @param check
     * @param user
     */
    @Transactional
    public void initSaleCheck(SellBackCheck check, User user) throws BizException {
        SellOrderBack order = sellOrderBackMapper.selectByPrimaryKey(check.getBackOrderId());
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            logger.warn("get sell order back fail or user company and order company is not match. orderId:{}", check.getBackOrderId());
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        //查询系统配置中是否有销售经理初审的流程，如果有，并且状态为INIT，则修改为INIT_SALE_CHECKED状态,否则其他情况下只做记录不修改状态
        boolean smFlow = systemConfigService.haveOrderFlow(user.getCompanyId(), ConfigKey.SALE_BACK_SM_CHECK);
        String status = order.getStatus();
        if(smFlow && SellBackStatus.INIT.name().equalsIgnoreCase(status)) {
            status = SellBackStatus.INIT_SALE_CHECKED.name();
        }
        logger.info("sale check status:{}, id:{}", status, order.getId());
        order.setStatus(status);
        order.setBackSaleUser(user.getNickname());
        order.setBackSaleTime(new Date());
        order.setBackSaleResult(check.getCheckResult());
        order.setUpdatedBy(user.getNickname());
        order.setUpdatedTime(new Date());
        sellOrderBackMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 质量经理初审
     * @param check
     * @param user
     */
    @Transactional
    public void initQualityCheck(SellBackCheck check, User user) throws BizException {
        SellOrderBack order = sellOrderBackMapper.selectByPrimaryKey(check.getBackOrderId());
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            logger.warn("get sell order back fail or user company and order company is not match. orderId:{}", check.getBackOrderId());
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        //获取系统配置流程，如果配置的流程中存在有销售经理初审这步流程，需要验证销售经理是否已经审核完成
        Map<String, SystemConfig> configMap = systemConfigService.getConfigMap(user.getCompanyId());
        SystemConfig smFlowConfig = configMap.get(ConfigKey.SALE_BACK_SM_CHECK.name());
        SystemConfig qaFlowConfig = configMap.get(ConfigKey.SALE_BACK_QA_CHECK.name());
        if (smFlowConfig != null && "open".equalsIgnoreCase(smFlowConfig.getKeyValue())) {
            //配置流程中存在有销售经理审核，验证状态是否为销售经理已经审核通过
            if (!SellBackStatus.INIT_SALE_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
                logger.warn("order:{} is not in INIT_SALE_CHECKED status, check cannot do. user:{}", order.getId(), user.getId());
                throw new BizException(ErrorCode.SELL_BACK_INIT_QUALITY_CHECK_ERROR);
            }
        }
        //判断是否存在有质管经理审核流程，如果有,状态只有在INIT或者INIT_SALE_CHECKED的情况下才做变更，否则，保持原有状态不变
        String status = order.getStatus();
        if (qaFlowConfig != null && "open".equalsIgnoreCase(qaFlowConfig.getKeyValue())) {
            if (SellBackStatus.INIT.name().equalsIgnoreCase(status) || SellBackStatus.INIT_SALE_CHECKED.name().equalsIgnoreCase(status)) {
                status = SellBackStatus.INIT_QUALITY_CHECKED.name();
            }
        }
        logger.info("init quality check status:{}, id:{}", status, order.getId());

        //如果状态正确，直接修改状态和登记审核信息
        order.setStatus(status);
        order.setBackQualityUser(user.getNickname());
        order.setBackQualityTime(new Date());
        order.setBackQualityResult(check.getCheckResult());
        order.setUpdatedBy(user.getNickname());
        order.setUpdatedTime(new Date());
        sellOrderBackMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 质量复核
     * @param check
     * @param user
     * @return 1 前端页面需要刷新订单列表，0：只有刷新详情
     * @throws BizException
     */
    public int qualityCheck(SellBackCheck check, User user) throws BizException {
        SellOrderBack order = sellOrderBackMapper.selectByPrimaryKey(check.getBackOrderId());
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            logger.warn("get sell order back fail or user company and order company is not match. orderId:{}", check.getBackOrderId());
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        if (!SellBackStatus.BACK_RECEIVE.name().equalsIgnoreCase(order.getStatus()) &&
                !SellBackStatus.QUALITY_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("order:{} is not in BACK_RECEIVE or QUALITY_CHECKED status, check cannot do. user:{}", order.getId(), user.getId());
            throw new BizException(ErrorCode.SELL_BACK_QUALITY_CHECK_ERROR);
        }
        //看是否存在有detailId, 如果存在，认为是验收一条详情的数据
        if (check.getDetailId() != null) {
            return qualityCheckDetail(order, check, user);
        }else {
            return qualityCheckOrder(order, check, user, false);
        }
    }

    @Transactional
    public int qualityCheckDetail(SellOrderBack order, SellBackCheck check, User user) throws BizException {
        if (check.getRightQuantity() == null || check.getRightQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException(ErrorCode.SELL_BACK_RIGHT_QUANTITY_ERROR);
        }
        SellOrderBackDetail detail = sellOrderBackDetailMapper.selectByPrimaryKey(check.getDetailId());
        if (detail == null) {
            throw new BizException(ErrorCode.SELL_BACK_DETAIL_GET_FAIL);
        }
        if (check.getRightQuantity().compareTo(detail.getBackQuantity()) > 0) {
            throw new BizException(ErrorCode.SELL_BACK_RIGHT_QUANTITY_ERROR);
        }
        check.setCheckStatus(true);
        check.setCheckUser(user.getNickname());
        check.setUpdatedBy(user.getNickname());
        check.setUpdatedTime(new Date());
        int count = sellOrderBackDetailMapper.updateCheckStatus(check);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        logger.info("update detail check success.");
        //查询订单是否已经验收完成，如果是，订单状态修改到验收完成状态
        List<SellOrderBackDetail> details = sellOrderBackDetailMapper.getByBackOrderId(order.getId());
        boolean result = true;
        for (SellOrderBackDetail item : details) {
            if (item.getCheckStatus() == null || !item.getCheckStatus()) {
                result = false;
                break;
            }
        }
        if (result) {
            logger.info("detail is check all success");
            return qualityCheckOrder(order, check, user, true);
        }else {
            return 0; //单笔处理时，前端页面不需要全部刷新订单，只刷新详情
        }
    }

    @Transactional
    public int qualityCheckOrder(SellOrderBack order, SellBackCheck check, User user, boolean isDetailCheck) {
        //看看是否有终审流程，如果有，当前状态变更为质检复核通过，如果没有，设置为终审通过
        boolean haveFnFlow = systemConfigService.haveOrderFlow(user.getCompanyId(), ConfigKey.SALE_BACK_FINAL_CHECK);
        String status = SellBackStatus.QUALITY_CHECKED.name();
        if (!haveFnFlow) {
            status = SellBackStatus.FINAL_CHECKED.name();
            order.setFinalCheckUser(user.getNickname());
            order.setFinalCheckTime(new Date());
        }
        if (!isDetailCheck) {
            //如果不是单独明细的审核，需要直接修改所有的详情验收结果和订单状态
            check.setUpdatedTime(new Date());
            check.setUpdatedBy(user.getNickname());
            check.setCheckUser(user.getNickname());
            check.setCheckStatus(true);
            int detailCount = sellOrderBackDetailMapper.updateCheckStatusByBackOrderId(check);
            if (detailCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }
        logger.info("update detail check success by orderId:{}", check.getBackOrderId());
        order.setStatus(status);
        order.setQualityCheckUser(user.getNickname());
        order.setQualityCheckTime(new Date());
        order.setUpdatedTime(new Date());
        order.setUpdatedBy(user.getNickname());
        int count = sellOrderBackMapper.updateByPrimaryKeySelective(order);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }

        //如果是终审通过的流程，需要变更库存信息，并且生产对应的事件
        if (SellBackStatus.FINAL_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.info("sell back order quality check and flow is end, then update repertory info. sell back order id:{}", order.getId());
            int repertoryCount = repertoryInfoMapper.sellBackOrderAddQuantity(order.getId());
            if (repertoryCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
            // 生产一个销售退货的事件
            rabbitmqQueueConfig.sendMessage("SellOrderBackService", RabbitmqQueueConfig.ORDER_SELL_BACK, order);
        }
        return 1; //成功处理，前端页面需要全部刷新订单
    }


    /**
     * 质量复核取消
     * @param check
     * @param user
     * @throws BizException
     */
    @Transactional
    public void cancelQualityCheck(SellBackCheck check, User user) throws BizException {
        SellOrderBack order = sellOrderBackMapper.selectByPrimaryKey(check.getBackOrderId());
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            logger.warn("get sell order back fail or user company and order company is not match. orderId:{}", check.getBackOrderId());
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        if (!SellBackStatus.QUALITY_CHECKED.name().equalsIgnoreCase(order.getStatus())
                && !SellBackStatus.BACK_RECEIVE.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("order:{} is not in QUALITY_CHECKED status, check cannot do. user:{}", order.getId(), user.getId());
            throw new BizException(ErrorCode.SELL_BACK_QUALITY_CHECK_CANCEL_ERROR);
        }

        //如果存在详情ID，则认为是取消一笔详情
        Long detailId = check.getDetailId();
        SellOrderBackDetail detail = null;
        if (detailId != null) {
            detail = sellOrderBackDetailMapper.selectByPrimaryKey(detailId);
            if (detail == null) {
                logger.warn("get detail fail by id:{}", detail);
                throw new BizException(ErrorCode.SELL_BACK_DETAIL_GET_FAIL);
            }
        }

        // 修改状态和登记质量验收意见
        order.setStatus(SellBackStatus.BACK_RECEIVE.name());
        order.setUpdatedTime(new Date());
        order.setUpdatedBy(user.getNickname());
        int count = sellOrderBackMapper.updateByPrimaryKeySelective(order);
        if (count <= 0) {
            logger.warn("update status fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        //登记详情的审核状态和意见取消
        check.setBackOrderId(order.getId());
        check.setDetailId(detail == null ? null : detail.getId());
        check.setBadPlan(null);
        check.setBadReason(null);
        check.setCheckResult(null);
        check.setUpdatedBy(user.getNickname());
        check.setUpdatedTime(new Date());
        check.setCheckStatus(false);
        check.setCheckTempMethod(null);
        check.setCheckTime(null);
        check.setCheckUser(null);
        int detailCount = sellOrderBackDetailMapper.updateCheckStatus(check);
        if (detailCount <= 0) {
            logger.warn("update sell back order detail check status fail. backOrderId:{}", order.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
    }

    /**
     * 终审
     * @param check
     * @param user
     * @throws BizException
     */
    @Transactional
    public void finalCheck(SellBackCheck check, User user) throws BizException {
        SellOrderBack order = sellOrderBackMapper.selectByPrimaryKey(check.getBackOrderId());
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            logger.warn("get sell order back fail or user company and order company is not match. orderId:{}", check.getBackOrderId());
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        if (SellBackStatus.FINAL_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            throw new BizException(ErrorCode.SELL_BACK_IS_FINAL_CHECKED);
        }

        //如果存在有质检复核流程，需要是质检复核通过的状态
        boolean haveQCFlow = systemConfigService.haveOrderFlow(user.getCompanyId(), ConfigKey.SALE_BACK_QUALITY_CHECK);
        if (haveQCFlow && !SellBackStatus.QUALITY_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("have quality check flow but status is not QUALITY_CHECKED, ID:{}", order.getId());
            throw new BizException(ErrorCode.SELL_BACK_NEED_QUALITY_CHECK);
        }else {
            //没有质量复核流程，至少需要是收货状态
            if (!SellBackStatus.BACK_RECEIVE.name().equalsIgnoreCase(order.getStatus())) {
                throw new BizException(ErrorCode.SELL_BACK_NEED_RECEIVE_STATUS);
            }
        }
        logger.info("sell back order do FINAL_CHECKED id:{}", order.getId());
        // 修改状态为终审通过
        order.setStatus(SellBackStatus.FINAL_CHECKED.name());
        order.setFinalCheckTime(new Date());
        order.setFinalCheckUser(user.getNickname());
        order.setUpdatedBy(user.getNickname());
        order.setUpdatedTime(new Date());
        int count = sellOrderBackMapper.updateByPrimaryKeySelective(order);
        if (count <= 0) {
            logger.warn("update back order status to final_check fail. orderId:{}", order.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }

        //如果是终审通过的流程，需要变更库存信息，并且生产对应的事件
        int repertoryCount = repertoryInfoMapper.sellBackOrderAddQuantity(order.getId());
        if (repertoryCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        // 生产一个销售退货的事件
        rabbitmqQueueConfig.sendMessage("SellOrderBackService", RabbitmqQueueConfig.ORDER_SELL_BACK, order);
    }

    /**
     * 删除销售退款单
     * @param backOrderId
     * @param user
     * @throws BizException
     */
    @Transactional
    public void removeBackOrder(Long backOrderId, User user) throws BizException {
        SellOrderBack order = sellOrderBackMapper.selectByPrimaryKey(backOrderId);
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            logger.warn("get sell order back fail or user company and order company is not match. orderId:{}", backOrderId);
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        if (SellBackStatus.FINAL_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("order:{} is in FINAL_CHECKED status, remove cannot do. user:{}", order.getId(), user.getId());
            throw new BizException(ErrorCode.SELL_BACK_REMOVE_STATUS_ERROR);
        }
        //直接修改到删除状态
        order.setStatus(SellBackStatus.DELETE.name());
        order.setUpdatedBy(user.getNickname());
        order.setUpdatedTime(new Date());
        int count = sellOrderBackMapper.updateByPrimaryKeySelective(order);
        if (count <= 0) {
            logger.warn("remove back order update status fail. order:{}", order.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        //把销售单明细中的退货数减去
        int count1 = sellOrderBackDetailMapper.updateAlreadyBackQuantity(order.getRefOrderId());
        if (count1 <= 0) {
            logger.warn("update sell order detail back quantity fail by backOrderId:{}", order.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
    }

    /**
     * 销售退货收货
     * @param back
     * @param user
     */
    @Transactional
    public void receive(SellOrderBack back, User user) throws BizException {
        SellOrderBack order = sellOrderBackMapper.selectByPrimaryKey(back.getId());
        if (order == null || !order.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get back order fail by id:{}", back.getId());
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        if (SellBackStatus.FINAL_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.info("sale back order status is FINAL_CHECKED, can not do receive action.");
            throw new BizException(ErrorCode.SELL_BACK_RECEIVE_STATUS_ERROR);
        }
        //获取系统配置流程
        Map<String, SystemConfig> configMap = systemConfigService.getConfigMap(user.getCompanyId());
        SystemConfig smFlow = configMap.get(ConfigKey.SALE_BACK_SM_CHECK.name());
        SystemConfig qaFlow = configMap.get(ConfigKey.SALE_BACK_QA_CHECK.name());
        SystemConfig qcFlow = configMap.get(ConfigKey.SALE_BACK_QUALITY_CHECK.name());
        SystemConfig fnFlow = configMap.get(ConfigKey.SALE_BACK_FINAL_CHECK.name());

        //如果存在销售经理验证流程，需要销售经理验收信息存在
        if (smFlow != null && "open".equalsIgnoreCase(smFlow.getKeyValue())
                && StringUtils.isEmpty(order.getBackSaleUser())) {
            logger.warn("sale back order need sale manage check id:{}", order.getId());
            throw new BizException(ErrorCode.SELL_BACK_NEED_SALE_MANAGE_CHECK);
        }
        //如果存在质管经理验收流程，需要先经过质管经理验收信息存在
        if (qaFlow != null && "open".equalsIgnoreCase(qaFlow.getKeyValue())
                && StringUtils.isEmpty(order.getBackQualityUser())) {
            logger.warn("sale back order need quality manage check id:{}", order.getId());
            throw new BizException(ErrorCode.SELL_BACK_NEED_QA_MANAGE_CHECK);
        }
        //根据配置的流程，看看质检复核和终审是否存在，来确认保存的状态
        boolean haveQCFlow = false;
        boolean haveFnFlow = false;
        if (qcFlow != null && "open".equalsIgnoreCase(qcFlow.getKeyValue())) {
            haveQCFlow = true;
        }
        if (fnFlow != null && "open".equalsIgnoreCase(fnFlow.getKeyValue())) {
            haveFnFlow = true;
        }
        String status = SellBackStatus.BACK_RECEIVE.name();
        if (!haveQCFlow && !haveFnFlow) {
            //如果两个流程都没有了，那直接就是收货入库操作,直接赋值到终审通过状态
            status = SellBackStatus.FINAL_CHECKED.name();
        }else {
            //只要有下一步的流程，直接设置为收货状态
            status = SellBackStatus.BACK_RECEIVE.name();
        }
        //如果存在了，直接调用修改逻辑
        back.setStatus(status);
        if (SellBackStatus.FINAL_CHECKED.name().equalsIgnoreCase(status)) {
            back.setFinalCheckTime(new Date());
            back.setFinalCheckUser(user.getNickname());
        }
        back.setUpdatedBy(user.getNickname());
        back.setUpdatedTime(new Date());
        int count = sellOrderBackMapper.updateByPrimaryKeySelective(back);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        //如果是终审通过的流程，需要变更库存信息，并且生产对应的事件
        if (SellBackStatus.FINAL_CHECKED.name().equalsIgnoreCase(back.getStatus())) {
            logger.info("sell back order receive and flow is end, then update repertory info. sell back order id:{}", back.getId());
            int repertoryCount = repertoryInfoMapper.sellBackOrderAddQuantity(back.getId());
            if (repertoryCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
            // 生产一个销售退货的事件
            rabbitmqQueueConfig.sendMessage("SellOrderBackService", RabbitmqQueueConfig.ORDER_SELL_BACK, back);
        }
    }

    public SellOrderBack backOrderView(Long backId, User user) throws BizException {
        SellOrderBack orderBack = sellOrderBackMapper.getViewBackOrderById(backId);
        if (orderBack == null || !user.getCompanyId().equals(orderBack.getCompanyId())) {
            throw new BizException(ErrorCode.SELL_BACK_ORDER_GET_FAIL);
        }
        List<SellOrderBackDetail> details = getDetails(orderBack.getId());
        orderBack.setDetails(details);
        return orderBack;
    }
}
