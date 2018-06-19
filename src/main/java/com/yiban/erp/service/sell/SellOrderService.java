package com.yiban.erp.service.sell;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.constant.*;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.*;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.goods.GoodsService;
import com.yiban.erp.service.util.SystemConfigService;
import com.yiban.erp.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SellOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderService.class);

    @Autowired
    private SellOrderMapper sellOrderMapper;
    @Autowired
    private SellOrderDetailMapper sellOrderDetailMapper;
    @Autowired
    private SellOrderShipMapper sellOrderShipMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private OptionsMapper optionsMapper;
    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private RepertoryOutMapper repertoryOutMapper;
    @Autowired
    private RepertoryOutDetailMapper repertoryOutDetailMapper;
    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private GoodsBlackListMapper goodsBlackListMapper;
    @Autowired
    private SellOrderPaymentMapper sellOrderPaymentMapper;
    @Autowired
    private SystemConfigService systemConfigService;


    /**
     * 主要验证销售客户是否配置了地域限制和销售限制，如果有，返回
     *
     * @param order
     * @throws BizException
     */
    public List<String> orderValidate(SellOrder order, User user) throws BizException {
        List<String> errorList = new ArrayList<>();

        //先获取客户信息
        Customer customer = customerMapper.getCustomerDetailById(user.getCompanyId(), order.getCustomerId());
        if (customer == null || customer.getEnabled() == null || !customer.getEnabled()) {
            logger.warn("customer get fail. customerId:{}", order.getCustomerId());
            throw new BizException(ErrorCode.CUSTOMER_GET_FAIL_OR_UNENABLE);
        }
        //获取商品的详情ID
        List<SellOrderDetail> orderDetails = order.getDetails();
        if (orderDetails == null || orderDetails.isEmpty()) {
            //空列表直接返回通过
            return errorList;
        }
        List<Long> goodsDetailIdList = new ArrayList<>();
        orderDetails.stream().forEach(item -> goodsDetailIdList.add(item.getGoodsId()));
        if (goodsDetailIdList.isEmpty()) {
            return errorList;
        }
        //获取所有选择的商品中的商品限制规则
        List<GoodsBlackListWithBLOBs> blackLists = goodsBlackListMapper.getByGoodsDetailIds(goodsDetailIdList);
        // 返回时返回所有的不合规的规则列表
        if (blackLists == null || blackLists.isEmpty()) {
            logger.info("goodsIds black list is empty.");
            return errorList;
        }

        //先验证是否存在客户不在禁止销售列表中
        for (GoodsBlackListWithBLOBs black : blackLists) {
            //客户限制
            Map<Long, Customer> customerBlack = black.getCustomerMap();
            if (customerBlack.containsKey(customer.getId())) {
                //解析，并且查询当前客户是否在列表中
                String error = "商品“" + black.getGoodsName() + "”配置有对客户“" + customer.getName() + "”限制条件";
                errorList.add(error);
            }

            //客户类别限制
            Set<Integer> custCatSet = black.getCustomerCategoryIdSet();
            if (customer.getCategoryId() != null && custCatSet.contains(customer.getCategoryId())) {
                String error = "商品：" + black.getGoodsName() + "配置有客户类“" + customer.getCategoryName() + "”限制条件";
                errorList.add(error);
            }

            //地域限制
            if (customer.getPlaceCodes() != null && black.isInRegionsBlack(customer.getPlaceCodes())) {
                //存在地域限制
                String error = "商品“" + black.getGoodsName() + "”配置有对客户“" + customer.getName() + "”的地域限制条件";
                errorList.add(error);
            }

        }

        return errorList;
    }

    private void validateGoodsRequirement(Customer customer, SellOrder sellOrder) throws BizException {
        //如果存在空的数据，需要验证是否存在有冷链经营性商品，如果有，返回验证不通过
        Set<Long> goodsInfoIds = new HashSet<>();
        Map<Long, BigDecimal> goodsQuantityMap = new HashMap<>();
        for (SellOrderDetail detail : sellOrder.getDetails()) {
            goodsInfoIds.add(detail.getGoodsId());
            if (detail.getGoods() != null) {
                goodsQuantityMap.put(detail.getGoods().getGoodsInfoId(), detail.getQuantity());
            }
        }
        //查询是否存在这些商品ID中是否存在冷链经营类型的商品
        List<GoodsInfo> goodsInfos = goodsService.getGoodsInfoListByIds(goodsInfoIds);
        boolean haveSpecial = false;
        boolean haveCold = false;
        for (GoodsInfo goodsInfo : goodsInfos) {
            if (Boolean.TRUE.equals(goodsInfo.getSpecialManage())) {
                haveSpecial = true;
            }

            if (Boolean.TRUE.equals(goodsInfo.getColdManage())) {
                haveCold = true;
            }

            if (goodsQuantityMap.containsKey(goodsInfo.getId()) && goodsInfo.getMinOrderLimit() != null) {
                if (goodsQuantityMap.get(goodsInfo.getId()).intValue() < goodsInfo.getMinOrderLimit()) {
                    logger.warn("not meet min order limit requirement, min={}, goodsInfoId={}", goodsInfo.getMinOrderLimit(), goodsInfo.getId());
                    throw new BizException(ErrorCode.SELL_ORDER_MIN_LIMIT_ERROR, goodsInfo.getName());
                }
            }
        }

        //验证供应商是否有特殊药品资质
        if (haveSpecial && (customer.getCanSaleSpecial() == null || !customer.getCanSaleSpecial())) {
            logger.warn("customer can not cold manage. customer:{}", customer.getId());
            throw new BizException(ErrorCode.SELL_ORDER_SPECIAL_VALIDATE);
        }

        //验证客户是否有改资质
        if (haveCold) {
            if (customer.getColdBusiness() == null || !customer.getColdBusiness()) {
                logger.warn("customer can not cold manage. supplier:{}", customer.getId());
                throw new BizException(ErrorCode.SELL_ORDER_COLD_VALIDATE);
            }

            //先看下订单的温控方式和运输方式是否都已经输入了，如果都输入了，没必要再验证
            if (sellOrder.getTemperControlId() == null || sellOrder.getTemperControlId() <= 0
                    || sellOrder.getShipMethod() == null || sellOrder.getShipMethod() <= 0) {
                throw new BizException(ErrorCode.SELL_ORDER_COLD_VALIDATE);
            }
        }

    }

    private void validateColdManage(Customer customer, SellOrder sellOrder) throws BizException {
        //如果存在空的数据，需要验证是否存在有冷链经营性商品，如果有，返回验证不通过
        List<Long> goodsIds = new ArrayList<>();
        for (SellOrderDetail detail : sellOrder.getDetails()) {
            goodsIds.add(detail.getGoodsId());
        }
        //查询是否存在这些商品ID中是否存在冷链经营类型的商品
        boolean haveCold = goodsService.haveColdManageGoods(goodsIds);
        if (!haveCold) {
            return;
        }
        //验证客户是否有改资质
        if (customer.getColdBusiness() == null || !customer.getColdBusiness()) {
            logger.warn("customer can not cold manage. supplier:{}", customer.getId());
            throw new BizException(ErrorCode.SELL_ORDER_COLD_VALIDATE);
        }

        //先看下订单的温控方式和运输方式是否都已经输入了，如果都输入了，没必要再验证
        if (sellOrder.getTemperControlId() == null || sellOrder.getTemperControlId() <= 0
                || sellOrder.getShipMethod() == null || sellOrder.getShipMethod() <= 0) {
            throw new BizException(ErrorCode.SELL_ORDER_COLD_VALIDATE);
        }
    }

    @Transactional
    public void orderSave(User user, SellOrder sellOrder) throws BizException {
        if (!validateSellOrder(sellOrder)) {
            logger.warn("user:{} save order but order validate is error.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_PARAM_ERROR);
        }
        //总金额和免零金额都不能小于0
        if (sellOrder.getTotalAmount() != null && BigDecimal.ZERO.compareTo(sellOrder.getTotalAmount()) > 0) {
            throw new BizException(ErrorCode.SELL_TOTAL_AMOUNT_ERROR);
        }
        if (sellOrder.getFreeAmount() != null && BigDecimal.ZERO.compareTo(sellOrder.getFreeAmount()) > 0) {
            throw new BizException(ErrorCode.SELL_FREE_AMOUNT_ERROR);
        }
        Customer customer = customerMapper.selectByPrimaryKey(sellOrder.getCustomerId());
        if (customer == null) {
            logger.warn("get customer fail by customerId: {}", sellOrder.getCustomerId());
            throw new BizException(ErrorCode.CUSTOMER_GET_FAIL);
        }
        if (!SellOrderStatus.TEMP_STORAGE.name().equalsIgnoreCase(sellOrder.getStatus())) {
            // 不是暂存操作，验证客户是否允许经营特殊管理药品 同时统计总销售数量和总金额

            validateGoodsRequirement(customer, sellOrder);

            //根据系统配置的流程，设置对应的状态
            boolean haveQAFlow = systemConfigService.haveOrderFlow(user.getCompanyId(), ConfigKey.SALE_CHECK);
            if (haveQAFlow) {
                logger.info("have QA flow, status update to INIT");
                sellOrder.setStatus(SellOrderStatus.INIT.name());
            } else {
                logger.info("have not QA flow, status update to QUALITY_CHECKED");
                sellOrder.setStatus(SellOrderStatus.QUALITY_CHECKED.name());
            }
        }

        List<SellOrderDetail> details = sellOrder.getDetails();
        BigDecimal totalQuantity = BigDecimal.ZERO;

        List<SellOrderDetail> realDetails = new ArrayList<>();

        for (SellOrderDetail item : details) {
            totalQuantity = totalQuantity.add(item.getQuantity  () == null ? BigDecimal.ZERO : item.getQuantity());

            if (item.getRepertoryId() == null) {
                // 展开,先进先出
                RepertorySelectQuery query = new RepertorySelectQuery();
                query.setCompanyId(user.getCompanyId());
                query.setGoodsId(item.getGoodsId());
                query.setMinQuantity(BigDecimal.ZERO);
                List<RepertoryInfo> repertoryInfos = repertoryInfoMapper.querySelectList(query);

                BigDecimal itemQuantity = item.getQuantity();
                for (RepertoryInfo ri : repertoryInfos) {
                    BigDecimal quantity = itemQuantity.compareTo(ri.getQuantity()) > 0 ? ri.getQuantity() : itemQuantity;

                    SellOrderDetail detail = null;
                    try {
                        detail = item.clone();
                    } catch (CloneNotSupportedException e) {
                        logger.error("Cannot clone sell order detail", e);
                        throw new BizException(ErrorCode.SELL_REPERTORY_NOT_ENOUGH);
                    }
                    detail.setRepertoryId(ri.getId());
                    detail.setQuantity(quantity);
                    detail.setBatchCode(ri.getBatchCode());
                    detail.setProductDate(ri.getProductDate());
                    detail.setExpDate(ri.getExpDate());
                    detail.setLocation(ri.getLocation());
                    realDetails.add(detail);

                    if (itemQuantity.compareTo(ri.getQuantity()) <= 0) {
                        // stop here
                        itemQuantity = BigDecimal.ZERO;
                        break;
                    } else {
                        // continue next loop
                        itemQuantity = itemQuantity.subtract(ri.getQuantity());
                    }

                }
                if (itemQuantity.compareTo(BigDecimal.ZERO) > 0) {
                    logger.warn("item quantity not enough: {}", sellOrder.getTotalQuantity());
                    throw new BizException(ErrorCode.SELL_REPERTORY_NOT_ENOUGH);
                }
            } else {
                realDetails.add(item);
            }
        }

        if (sellOrder.getId() == null) {
            logger.info("new sell order save.");
            String orderNumber = UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.SELL);
            //计算订单的总数量和总金额
            sellOrder.setCompanyId(user.getCompanyId());
            sellOrder.setOrderNumber(orderNumber);
            sellOrder.setCreateBy(user.getNickname());
            sellOrder.setCreateTime(new Date());
            sellOrder.setTotalQuantity(totalQuantity);
            sellOrder.setTotalAmount(sellOrder.getTotalAmount() == null ? BigDecimal.ZERO : sellOrder.getTotalAmount());
            sellOrder.setFreeAmount(sellOrder.getFreeAmount() == null ? BigDecimal.ZERO : sellOrder.getFreeAmount());
            sellOrder.setDisRate(sellOrder.getDisRate() == null ? BigDecimal.valueOf(100.0) : sellOrder.getDisRate());
            // 保存销售订单
            int count = sellOrderMapper.insert(sellOrder);
            // 保存销售订单详情
            if (count > 0 && sellOrder.getId() > 0) {
                //保存详情信息
                realDetails.stream().forEach(item -> {
                    item.setSellOrderId(sellOrder.getId());
                    item.setCreateBy(user.getNickname());
                    item.setCreateTime(new Date());
                    item.setUpdateBy(user.getNickname());
                    item.setUpdateTime(new Date());
                });
                sellOrderDetailMapper.replaceBatch(realDetails);
            }
        } else {
            sellOrder.setTotalQuantity(totalQuantity);
            sellOrder.setTotalAmount(sellOrder.getTotalAmount() == null ? BigDecimal.ZERO : sellOrder.getTotalAmount());
            sellOrder.setFreeAmount(sellOrder.getFreeAmount() == null ? BigDecimal.ZERO : sellOrder.getFreeAmount());
            sellOrder.setDisRate(sellOrder.getDisRate() == null ? BigDecimal.valueOf(100.0) : sellOrder.getDisRate());
            sellOrder.setUpdateTime(new Date());
            sellOrder.setUpdateBy(user.getNickname());
            int count = sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
            realDetails.stream().forEach(item -> {
                item.setUpdateBy(user.getNickname());
                item.setUpdateTime(new Date());
                item.setSellOrderId(sellOrder.getId());
                if (item.getCreateBy() == null) {
                    item.setCreateBy(user.getNickname());
                    item.setCreateTime(new Date());
                }
            });
            sellOrderDetailMapper.replaceBatch(realDetails);
        }
        logger.info("user:{} save order info success.", user.getId());
        List<SellOrderDetail> resultDetails = getDetailList(sellOrder.getId());

        for (SellOrderDetail detail : resultDetails) {
            repertoryInfoMapper.updateOnWayQuantity(detail.getRepertoryId());
        }
    }


    private boolean validateSellOrder(SellOrder order) {
        if (order == null) {
            logger.warn("request order is null.");
            return false;
        }
        if (order.getCustomerId() == null) {
            logger.warn("customer id is null");
            return false;
        }
        if (order.getCustomerRepId() == null) {
            logger.warn("customer rep id is null.");
            return false;
        }
        if (order.getSaleId() == null) {
            logger.warn("saler id is null.");
            return false;
        }
        if (order.getWarehouseId() == null) {
            logger.warn("warehouse id is null");
            return false;
        }
        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            logger.warn("good detail is null.");
            return false;
        }
        if (SellOrderStatus.INIT.name().equalsIgnoreCase(order.getStatus())) {
            for (SellOrderDetail detail : order.getDetails()) {
                //验证是否存在销售数量小于0的数据
                if (detail.getQuantity() == null || BigDecimal.ZERO.compareTo(detail.getQuantity()) >= 0) {
                    logger.warn("sell order detail quantity <= 0");
                    return false;
                }
            }
        }
        return true;
    }

    @Transactional
    public int removeSellOrder(User user, Long id) throws BizException {
        logger.info("user:{} remove sell order id:{}, set to delete status.", user.getId(), id);
        //验证是否存在质量审核通过的商品，如果存在，不能删除
        SellOrder order = sellOrderMapper.selectByPrimaryKey(id);
        if (order == null) {
            logger.warn("get order fail by id:{}", id);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        if (SellOrderStatus.SALE_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("sell order is already checked, can not delete. id:{}", id);
            throw new BizException(ErrorCode.SELL_ORDER_REMOVE_STATUS_ERROR);
        }
        if (!SellOrderStatus.TEMP_STORAGE.name().equalsIgnoreCase(order.getStatus())) {
            //修改存在的在单数，在暂存的状态下没有统计在单数
            int count = repertoryInfoMapper.sellOrderReleaseOnWayQuantity(order.getId());
            if (count <= 0) {
                logger.warn("release sell order on way quantity fail. sellOrderId:{}", order.getId());
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }
        order.setStatus(SellOrderStatus.DELETE.name());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        return sellOrderMapper.updateByPrimaryKeySelective(order);
    }

    public List<SellOrderDetail> getDetailList(Long sellOrderId) {
        if (sellOrderId == null) {
            logger.warn("get sell order detail by sell order id is null.");
            return Collections.emptyList();
        }
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(sellOrderId);
        if (sellOrder == null) {
            logger.warn("get sell order detail but sell order id is error. id:{}", sellOrderId);
            return Collections.emptyList();
        }
        List<SellOrderDetail> details = sellOrderDetailMapper.getDetailList(sellOrderId);
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        //如果存在，把对应的产品信息
        setDetailsGoods(details);
        return details;
    }

    public List<SellOrderDetail> getDetailHistory(Integer companyId, Long customerId, Long goodsId) {
        if (companyId == null || customerId == null || goodsId == null) {
            return Collections.emptyList();
        }
        List<SellOrderDetail> details = sellOrderDetailMapper.getDetailHistory(companyId, customerId, goodsId, 0, 50);
        if (details.isEmpty()) {
            return details;
        }
        setDetailsGoods(details);
        return details;
    }

    private void setDetailsGoods(List<SellOrderDetail> details) {
        //设置goods信息
        List<Long> goodsIdList = new ArrayList<>();
        details.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
        List<Goods> goods = goodsService.getGoodsById(goodsIdList);
        Map<Long, Goods> goodsMap = new HashMap<>();
        goods.stream().forEach(item -> goodsMap.put(item.getId(), item));
        details.stream().forEach(item -> item.setGoods(goodsMap.get(item.getGoodsId())));
    }

    public int removeSellOrderDetail(User user, Long detailId) throws BizException {
        if (detailId == null || detailId <= 0) {
            return 0;
        }
        SellOrderDetail detail = sellOrderDetailMapper.selectByPrimaryKey(detailId);
        if (detail == null) {
            logger.warn("user:{} get sell order detail by id:{} fail.", user.getId(), detailId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        //验证是否初始状态，如果审批过了，不能进行删除
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(detail.getSellOrderId());
        if (sellOrder == null || SellOrderStatus.SALE_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())) {
            logger.warn("user:{} remove sell order detail but sell order is null or status is not init. id:{}", user.getId(), detailId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_CAN_NOT_REMOVE);
        }
        logger.info("user:{} request to remove sell order detail by id:{}", user.getId(), detailId);
        return sellOrderDetailMapper.deleteByPrimaryKey(detailId);
    }

    public List<SellOrder> getReviewOrderList(SellReviewOrderQuery query) {
        if (query.getStatusList() == null || query.getStatusList().isEmpty()) {
            query.setStatusList(null);
        }
        List<SellOrder> sellOrders = sellOrderMapper.getReviewOrderList(query);
        setOptions(sellOrders);
        return sellOrders;
    }

    private void setOptions(List<SellOrder> sellOrders) {
        if (sellOrders == null || sellOrders.isEmpty()) {
            return;
        }
        Set<Long> set = new HashSet<>();
        sellOrders.stream().forEach(item -> set.addAll(item.getOptionsIds()));
        Long[] ids = new Long[set.size()];
        set.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        sellOrders.stream().forEach(item -> item.setOptionsName(options));
    }

    @Transactional
    public void qualityCheck(User user, SellReviewAction reviewAction) throws BizException {
        if (reviewAction == null || reviewAction.getCheckStatus() == null) {
            logger.warn("submit order review info but params is null.");
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_SUBMIT_PARAMS);
        }
        Long sellOrderId = reviewAction.getSellOrderId();
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(sellOrderId);
        if (sellOrder == null) {
            logger.warn("review order but can not get order by id:{}", sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        if (!SellOrderStatus.INIT.name().equalsIgnoreCase(sellOrder.getStatus())) {
            logger.warn("sell order status is not INIT, can not do quality check.");
            throw new BizException(ErrorCode.SELL_STATUS_QUALITY_ERROR);
        }
        reviewAction.setCheckDate(new Date());
        reviewAction.setUpdateBy(user.getNickname());
        reviewAction.setUpdateTime(new Date());
        sellOrderDetailMapper.updateCheckResult(reviewAction);

        String status = SellOrderDetailCheckStatus.OK.name().equalsIgnoreCase(reviewAction.getCheckStatus())
                ? SellOrderStatus.QUALITY_CHECKED.name() : SellOrderStatus.QUALITY_REJECT.name();

        //相应修改订单的状态, 出库质量审核
        logger.info("sell order:{} have check quality review ok", sellOrderId);
        sellOrder.setStatus(status);
        sellOrder.setUpdateBy(user.getNickname());
        sellOrder.setUpdateTime(new Date());
        sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
    }

    @Transactional
    public void qualityCheckCancel(User user, Long sellOrderId) throws BizException {
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(sellOrderId);
        if (sellOrder == null) {
            logger.warn("user:{} review cancel but sell order not found by id:{}", user.getId(), sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        if (!SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())
                && !SellOrderStatus.QUALITY_REJECT.name().equalsIgnoreCase(sellOrder.getStatus())) {
            logger.warn("current order status is can not cancel check order id:{}", sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_STATUS_ERROR);
        }
        //直接根据detailId和type进行删除审批记录信息
        SellReviewAction action = new SellReviewAction();
        action.setSellOrderId(sellOrderId);
        action.setCheckStatus(null);
        action.setCheckResult(null);
        action.setCheckDate(null);
        action.setCheckUser(null);
        action.setUpdateTime(new Date());
        action.setUpdateBy(user.getNickname());
        int count = sellOrderDetailMapper.updateCheckResult(action);
        if (count <= 0) {
            logger.warn("update sell order detail fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        //修改回待质审的状态
        sellOrder.setStatus(SellOrderStatus.INIT.name());
        sellOrder.setUpdateBy(user.getNickname());
        sellOrder.setUpdateTime(new Date());
        sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
    }

    @Transactional
    public void sellOrderCheckOk(User user, Long sellOrderId) throws BizException {
        if (sellOrderId == null) {
            logger.warn("user:{} check sell order sale but id is null", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        SellOrder order = sellOrderMapper.getReviewDetailById(sellOrderId);
        if (order == null) {
            logger.warn("user:{} check sell order sale but get order result is null", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        //验证订单状态是否为质检通过的状态，如果不是，不能进行审批
        if (!SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("user:{} check sell order but order status is not QUALITY_CHECKED, order id:{}", user.getId(), sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_SALE_CHECK_STATUS_ERROR);
        }
        List<SellOrderDetail> details = getDetailList(order.getId());
        if (details == null || details.isEmpty()) {
            logger.warn("get sell order detail fail.");
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }

        //验证当前仓库是否在正常状态，如果不在正常状态不能提交
        Warehouse warehouse = warehouseMapper.selectByPrimaryKey(order.getWarehouseId());
        if (!WarehouseStatus.NORMAL.name().equalsIgnoreCase(warehouse.getStatus())) {
            logger.warn("warehouse status is not normal.");
            throw new BizException(ErrorCode.SELL_ORDER_WAREHOUSE_FROZEN);
        }

        //先减去库存数据，如果库存不足，不能审批通过
        //获取库存不足的产品名称
        List<String> goodNameList = repertoryInfoMapper.getGoodNameWithLessQuantity(sellOrderId);
        if (goodNameList != null && !goodNameList.isEmpty()) {
            logger.error("user:{} check sell order:{} but repertory quantity is less then order quantity.", user.getId(), sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_QUANTITY_NOT_ENOUGH, goodNameList);
        }
        logger.info("user:{} check sell order and repertory enough quantity. orderId: {}", user.getId(), sellOrderId);
        //减库存
        int count = repertoryInfoMapper.sellOrderConsumeQuantity(sellOrderId, user.getNickname(), new Date());
        if (count <= 0) {
            logger.warn("update repertory fail .");
            throw new BizRuntimeException(ErrorCode.SELL_ORDER_QUANTITY_NOT_ENOUGH, goodNameList);
        }
        logger.info("user:{} consume repertory quantity update record success count:{}, orderId:{}", user.getId(), count, sellOrderId);
        //变更状态
        order.setStatus(SellOrderStatus.SALE_CHECKED.name());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        sellOrderMapper.updateByPrimaryKeySelective(order);

        // 生成一笔出库单信息 和发出对应事件，使之生成对应的财务往来账
        checkOkAfter(user, order, details);
    }

    @Transactional
    public void checkOkAfter(User user, SellOrder order, List<SellOrderDetail> details) {
        RepertoryOut out = new RepertoryOut();
        out.setCompanyId(order.getCompanyId());
        out.setWarehouseId(order.getWarehouseId());
        out.setStatus(RepertoryOutStatus.CHECKED.name());
        out.setRefType(RepertoryRefType.SELL_BATCH.name());
        out.setOutDate(new Date());
        out.setRefOrderId(order.getId());
        out.setRefOrderNumber(order.getOrderNumber());
        out.setCustomerId(order.getCustomerId());
        out.setCustomerName(order.getCustomerName());
        out.setCustomerRepId(order.getCustomerRepId());
        out.setCustomerRepName(order.getCustomerRepName());
        out.setGoTo(order.getCustomerName());
        out.setTotalQuantity(order.getTotalQuantity());
        out.setTotalAmount(order.getTotalAmount());
        out.setMakeOrderUser(order.getCreateBy());
        out.setCheckOrderUser(user.getNickname());
        out.setCheckDate(new Date());
        out.setComment(order.getComment());
        out.setCreatedBy(user.getNickname());
        out.setCreatedTime(new Date());
        int count = repertoryOutMapper.insert(out);
        if (count <= 0 || out.getId() == null || out.getId() <= 0) {
            logger.error("insert repertory out record fail. orderId:{}", order.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB); //注意是RuntimeException,使上面修改回滚
        }
        List<RepertoryOutDetail> outDetails = new ArrayList<>();
        for (SellOrderDetail orderDetail : details) {
            RepertoryOutDetail outDetail = new RepertoryOutDetail();
            outDetail.setRepertoryOutId(out.getId());
            outDetail.setRepertoryInfoId(orderDetail.getRepertoryId());
            outDetail.setGoodsId(orderDetail.getGoodsId());
            outDetail.setBatchCode(orderDetail.getBatchCode());
            outDetail.setLocation(orderDetail.getLocation());
            outDetail.setProductDate(orderDetail.getProductDate());
            outDetail.setExpDate(orderDetail.getExpDate());
            outDetail.setQuantity(orderDetail.getQuantity());
            outDetail.setFree(orderDetail.getFree());
            outDetail.setPrice(orderDetail.getRealPrice());
            outDetail.setDisPrice(orderDetail.getDisPrice());
            outDetail.setAmount(orderDetail.getAmount());
            outDetail.setTaxRate(orderDetail.getTaxRate());
            outDetail.setCheckUser(orderDetail.getCheckUser());
            outDetail.setCheckDate(orderDetail.getCheckDate());
            outDetail.setCheckResult(orderDetail.getCheckResult());
            outDetail.setCreatedBy(user.getNickname());
            outDetail.setCreatedTime(new Date());
            outDetails.add(outDetail);
        }
        int detailCount = repertoryOutDetailMapper.insertBatch(outDetails);
        logger.info("repertory out:{} insert details size:{}", out.getId(), detailCount);

        // 创造一个消息放到MQ中，用于处理后续逻辑
        rabbitmqQueueConfig.sendMessage("SellOrderService", RabbitmqQueueConfig.ORDER_SELL, order);
    }


    public SellOrder reviewDetail(Long orderId) {
        SellOrder order = sellOrderMapper.getReviewDetailById(orderId);
        if (order == null) {
            return null;
        }
        Set<Long> optionIdSet = order.getOptionsIds();
        Long[] optionsIds = new Long[optionIdSet.size()];
        optionIdSet.toArray(optionsIds);
        List<Options> options = optionsMapper.getByIds(optionsIds);
        order.setOptionsName(options);
        List<SellOrderDetail> details = getDetailList(orderId);
        order.setDetails(details);
        return order;
    }

    public List<SellOrderShip> getOrderShipRecords(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return Collections.emptyList();
        }
        return sellOrderShipMapper.getBySellOrderId(orderId);
    }


    public Integer getOrderPaymentHistoryCount(SellOrderQuery query) {
        if (query == null || query.getCompanyId() == null || query.getSellOrderId() == null) {
            return 0;
        }
        return sellOrderPaymentMapper.getPaymentHistoryCount(query);
    }

    public List<SellOrderPayment> getOrderPaymentHistory(SellOrderQuery query) {
        if (query == null || query.getCompanyId() == null || query.getSellOrderId() == null) {
            return Collections.emptyList();
        }
        return sellOrderPaymentMapper.getPaymentHistory(query);
    }

    public SellOrderShip saveOrderShip(User user, SellOrderShip sellOrderShip) throws BizException {
        SellOrderShip reqShip = sellOrderShip;
        if (reqShip == null || reqShip.getSellOrderId() == null) {
            logger.warn("user:{} save ship info but sell order id or ship company id is null", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_SHIP_PARAMS);
        }
        SellOrder order = sellOrderMapper.selectByPrimaryKey(reqShip.getSellOrderId());
        if (order == null) {
            logger.warn("user:{} save ship record info but sell order get fail by id:{}", user.getId(), reqShip.getSellOrderId());
            throw new BizException(ErrorCode.SELL_ORDER_SHIP_PARAMS);
        }
        if (reqShip.getId() != null) {
            reqShip.setUpdateBy(user.getNickname());
            reqShip.setUpdateTime(new Date());
            sellOrderShipMapper.updateByPrimaryKeySelective(reqShip);
            return reqShip;
        } else {
            reqShip.setCreateBy(user.getNickname());
            reqShip.setCreateTime(new Date());
            sellOrderShipMapper.insert(reqShip);
            return reqShip;
        }
    }

    public int removeOrderShip(User user, Long id) throws BizException {
        if (id == null) {
            throw new BizException(ErrorCode.SELL_ORDER_SHIP_NOT_FUND);
        }
        SellOrderShip sellOrderShip = sellOrderShipMapper.selectByPrimaryKey(id);
        if (sellOrderShip == null) {
            throw new BizException(ErrorCode.SELL_ORDER_SHIP_NOT_FUND);
        }
        logger.info("user:{} remove sell ship record:{}", user.getId(), JSON.toJSONString(sellOrderShip));
        return sellOrderShipMapper.deleteByPrimaryKey(id);
    }

    public List<SellOrder> getAllList(SellOrderAllAction allAction) {
        List<SellOrder> sellOrders = sellOrderMapper.getAllList(allAction);
        setOptions(sellOrders);
        return sellOrders;
    }

    public List<StatusCount> getStatByCustomer(SellOrderAllAction query) {
        List<StatusCount> statusCounts = sellOrderMapper.getCustomerStat(query.getCompanyId(), query.getCustomerId(),
                query.getStartDate(), query.getEndDate());

        return statusCounts;
    }

    public boolean addPayment(Long orderId, SellOrderPayment payment, User user) throws BizException {
        if (orderId != null) {
            SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(orderId);
            if (sellOrder == null || "DELETE".equalsIgnoreCase(sellOrder.getStatus())) {
                throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
            }

            if (!sellOrder.getCompanyId().equals(user.getCompanyId())) {
                throw new BizException(ErrorCode.SELL_ORDER_COMPANY_NOT_MATCH);
            }

            if (sellOrder.getPaidAmount().compareTo(sellOrder.getTotalAmount()) >= 0) {
                throw new BizException(ErrorCode.SELL_ORDER_ALREADY_PAID);
            }

            int result = sellOrderMapper.updatePayment(orderId, payment.getPayAmount());
            if (result > 0) {
                return true;
            }
        }
        return false;
    }


    public SellOrder saveOrderInvoice(User user,SellOrderInvoice sellOrderInvoice) throws BizException {
        SellOrderInvoice reqInvoice = sellOrderInvoice;
        if (reqInvoice == null || reqInvoice.getSellOrderId() == null) {
            logger.warn("user:{} save ship info but sell order id or ship company id is null", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_SHIP_PARAMS);
        }
        SellOrder order = sellOrderMapper.selectByPrimaryKey(reqInvoice.getSellOrderId());
        if (order == null) {
            logger.warn("user:{} save ship record info but sell order get fail by id:{}", user.getId(), reqInvoice.getSellOrderId());
            throw new BizException(ErrorCode.SELL_ORDER_SHIP_PARAMS);
        }
        order.setBillStatus("FINISH");
        order.setBillType(reqInvoice.getBillType());
        order.setInvoiceTitle(reqInvoice.getInvoiceTitle());
        order.setTaxRate(reqInvoice.getTaxRate());
        sellOrderMapper.updateByPrimaryKeySelective(order);
        return order;

    }

}
