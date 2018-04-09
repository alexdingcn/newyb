package com.yiban.erp.service.sell;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.constant.SellOrderStatus;
import com.yiban.erp.constant.OptionsType;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.SellReviewAction;
import com.yiban.erp.dto.SellReviewOrderQuery;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.warehouse.RepertoryService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SellOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderService.class);

    @Autowired
    private SellOrderMapper sellOrderMapper;
    @Autowired
    private SellOrderDetailMapper sellOrderDetailMapper;
    @Autowired
    private RepertoryService repertoryService;
    @Autowired
    private SellOrderShipMapper sellOrderShipMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private OptionsMapper optionsMapper;

    public List<SellOrder> getList(Integer companyId, Integer customerId, Integer salerId,
                                            String refNo, String status, Date createOrderDate, Integer page, Integer size) {
        int limit = (size == null || size <= 0) ? 10 : size;
        int offset = (page == null || page <= 0 ? 0 : page - 1) * limit;
        return sellOrderMapper.getList(companyId, customerId, salerId, refNo, status,createOrderDate, limit, offset);
    }

    public SellOrder orderSave(User user, SellOrder sellOrder) throws BizException {
        if (!validateSellOrder(sellOrder)) {
            logger.warn("user:{} save order but order validate is error.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_PARAM_ERROR);
        }
        List<SellOrderDetail> details = sellOrder.getDetails();
        //验证客户是否允许经营特殊管理药品
        List<Long> goodIdList = new ArrayList<>();
        details.stream().forEach(item -> goodIdList.add(item.getGoodId()));
        if(!isCustomerCanSellGoods(sellOrder.getCustomerId(), goodIdList)) {
            logger.warn("user: {} save sell order detail good have special managed but customer:{} can not shell.", user.getId(), sellOrder.getCustomerId());
            throw new BizException(ErrorCode.SELL_ORDER_CUSTOMER_CANNOT_SELL_GOOD);
        }

        if (sellOrder.getId() == null) {
            logger.info("new sell order save.");
            String orderNumber = UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.SELL);
            sellOrder.setCompanyId(user.getCompanyId());
            sellOrder.setOrderNumber(orderNumber);
            sellOrder.setCreateBy(user.getNickname());
            sellOrder.setCreateTime(new Date());
            int count = sellOrderMapper.insertSelective(sellOrder);
            if (count > 0 && sellOrder.getId() > 0) {
                //保存详情信息
                details.stream().forEach(item -> {
                    item.setSellOrderId(sellOrder.getId());
                    item.setCreateBy(user.getNickname());
                    item.setCreateTime(new Date());
                    item.setUpdateBy(user.getNickname());
                    item.setUpdateTime(new Date());
                });
                sellOrderDetailMapper.replaceBatch(details);
            }
        }else {
            sellOrder.setUpdateTime(new Date());
            sellOrder.setUpdateBy(user.getNickname());
            int count = sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
            details.stream().forEach(item -> {
                item.setUpdateBy(user.getNickname());
                item.setUpdateTime(new Date());
                item.setSellOrderId(sellOrder.getId());
                if (item.getCreateBy() == null) {
                    item.setCreateBy(user.getNickname());
                    item.setCreateTime(new Date());
                }
            });
            sellOrderDetailMapper.replaceBatch(details);
        }
        logger.info("user:{} save order info success.", user.getId());
        SellOrder afterOrder = sellOrderMapper.selectByPrimaryKey(sellOrder.getId());
        List<SellOrderDetail> resultdetails = getDetailList(sellOrder.getId());
        afterOrder.setDetails(resultdetails);
        return afterOrder;
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
        if (order.getSalerId() == null) {
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
        return true;
    }

    public int removeSellOrder(User user, Long id) throws BizException {
        logger.info("user:{} remove sell order id:{}, set to delete status.", user.getId(), id);
        //验证是否存在质量审核通过的商品，如果存在，不能删除
        List<Long> checkOkDetailIds = sellOrderDetailMapper.getCheckOkDetailIdList(id);
        if (checkOkDetailIds != null && !checkOkDetailIds.isEmpty()) {
            logger.warn("user:{} remove sell order but have quality check ok detail:{}", user.getId(), JSON.toJSONString(checkOkDetailIds));
            throw new BizException(ErrorCode.SELL_ORDER_REMOVE_HAVE_OK_DETAIL);
        }
        SellOrder order = new SellOrder();
        order.setId(id);
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
        //如果存在，把对应的产品信息(对应的库存信息，库存信息中关联了产品信息)查询出来关联到对应的订单详情中
        List<Long> repertoryIds = new ArrayList<>();
        details.stream().forEach(item -> repertoryIds.add(item.getRepertoryId()));
        Map<Long, RepertoryInfo> repertoryInfoMap = repertoryService.getMapByIdList(repertoryIds);
        details.stream().forEach(item -> item.setRepertoryInfo(repertoryInfoMap.get(item.getRepertoryId())));
        return details;
    }

    public Map<Long,List<SellOrderDetail>> getDetailHistory(Integer companyId, Integer customerId, List<Long> goodIds) {
        if (companyId == null || customerId == null || goodIds == null || goodIds.isEmpty()) {
            return null;
        }
        List<SellOrderDetail> details = sellOrderDetailMapper.getDetailHistory(companyId, customerId, goodIds, 0, 20);
        if (details.isEmpty()) {
            return null;
        }
        //根据repertoryId获取对应库存产品信息
        List<Long> repertoryIds = new ArrayList<>();
        details.stream().forEach(item -> repertoryIds.add(item.getRepertoryId()));
        Map<Long, RepertoryInfo> infoMap = repertoryService.getMapByIdList(repertoryIds);
        if (infoMap != null) {
            details.stream().forEach(item -> item.setRepertoryInfo(infoMap.get(item.getRepertoryId())));
        }
        //根据商品的ID，进行分组
        Map<Long, List<SellOrderDetail>> result = new HashMap<>();
        for (SellOrderDetail detail : details) {
            RepertoryInfo repertoryInfo = detail.getRepertoryInfo();
            if (repertoryInfo == null || repertoryInfo.getGoodsId() == null) {
                continue;
            }
            Long goodId = repertoryInfo.getGoodsId();
            if (!result.containsKey(goodId)) {
                List<SellOrderDetail> itemList = new ArrayList<>();
                result.put(goodId, itemList);
            }
            result.get(goodId).add(detail);
        }
        return result;
    }


    public boolean isCustomerCanSellGoods(Integer customerId, List<Long> goodIds) throws BizException {
        Customer customer = customerMapper.selectByPrimaryKey(customerId);
        if (customer == null) {
            throw new BizException(ErrorCode.CUSTOMER_GET_FAIL);
        }
        if (customer.getCanSaleSpecial() != null && customer.getCanSaleSpecial()) {
            return true;
        }
        //验证产品列表中是否存在特殊管理药品
        List<Goods> goods = goodsMapper.selectByIdList(goodIds);
        for (Goods good : goods) {
            if (good.getSpecialManaged() != null && good.getSpecialManaged()) {
                return false;
            }
        }
        return true;
    }

    public int removeSellOrderDetail(User user, Long detailId) throws BizException{
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
        if (sellOrder == null || !SellOrderStatus.TEMP_STORAGE.name().equalsIgnoreCase(sellOrder.getStatus())) {
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
        return sellOrderMapper.getReviewOrderList(query);
    }

    public void qualityCheck(User user, SellReviewAction reviewAction) throws BizException {
        if (reviewAction == null) {
            logger.warn("submit order review info but params is null.");
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_SUBMIT_PARAMS);
        }
        Long sellOrderId = reviewAction.getSellOrderId();
        List<Long> detailIdList = reviewAction.getDetailIdList();
        if (sellOrderId == null || detailIdList.isEmpty()) {
            logger.warn("review order details but id is empty.");
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_SUBMIT_PARAMS);
        }
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(sellOrderId);
        if (sellOrder == null) {
            logger.warn("review order but can not get order by id:{}", sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        reviewAction.setCheckUser(user.getNickname());
        reviewAction.setCheckDate(new Date());
        sellOrderDetailMapper.updateCheckResult(detailIdList, reviewAction, new Date(), user.getNickname());

        List<Long> uncheckIds = sellOrderDetailMapper.getUnCheckDetailIdList(sellOrderId);
        if (!uncheckIds.isEmpty()) {
            logger.debug("have uncheck detail: {}", JSON.toJSONString(uncheckIds));
            return;
        }
        //如果完成了，相应修改订单的状态, 出库质量审核
        logger.info("sell order:{} have check quality review ok", sellOrderId);
        sellOrder.setStatus(SellOrderStatus.QUALITY_CHECKED.name());
        sellOrder.setUpdateBy(user.getNickname());
        sellOrder.setUpdateTime(new Date());
        sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
    }

    public int qualityCheckCancel(User user, SellReviewAction action) throws BizException {
        if (action == null || action.getDetailList() == null) {
            logger.warn("user:{} review cancel but params error.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_SUBMIT_PARAMS);
        }
        Long sellOrderId = action.getSellOrderId();
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(sellOrderId);
        if (sellOrder == null) {
            logger.warn("user:{} review cancel but sell order not found by id:{}", user.getId(), sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        if (!SellOrderStatus.INIT.name().equalsIgnoreCase(sellOrder.getStatus())
                && !SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())) {
            logger.warn("current order status is can not cancel check order id:{}", sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_STATUS_ERROR);
        }
        //直接根据detailId和type进行删除审批记录信息
        List<Long> detailIdList = action.getDetailIdList();
        action.setCheckStatus(null);
        action.setCheckResult(null);
        action.setCheckDate(null);
        action.setCheckUser(null);
        int count = sellOrderDetailMapper.updateCheckResult(detailIdList, action, new Date(), user.getNickname());
        //如果order的状态是已经审批完成的，改回上一级状态
        if (SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())) {
            sellOrder.setStatus(SellOrderStatus.INIT.name());
            sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
        }
        return count;
    }

    public void sellOrderCheckOk(User user, Long sellOrderId) throws BizException {
        if (sellOrderId == null) {
            logger.warn("user:{} check sell order sale but id is null", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        SellOrder order = sellOrderMapper.selectByPrimaryKey(sellOrderId);
        if (order == null) {
            logger.warn("user:{} check sell order sale but get order result is null", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        //验证订单状态是否为质检通过的状态，如果不是，不能进行审批
        if (!SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("user:{} check sell order but order status is not QUALITY_CHECKED, order id:{}", user.getId(), sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_SALE_CHECK_STATUS_ERROR);
        }
        //验证订单的质量检查是否都已经全部通过，只有全部通过，才能进行审核通过
        List<Long> detailIds = sellOrderDetailMapper.getUnCheckDetailIdList(sellOrderId);
        if (detailIds != null && !detailIds.isEmpty()) {
            logger.warn("user:{} check sell order but have quality check un ok.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_CHECK_SALE_HAVE_UNOK_DETAIL);
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
        logger.info("user:{} consume repertory quantity update record success count:{}, orderId:{}", user.getId(), count, sellOrderId);
        order.setStatus(SellOrderStatus.SALE_CHECKED.name());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        sellOrderMapper.updateByPrimaryKeySelective(order);
    }

    public SellOrder reviewDetail(Long orderId) {
        SellOrder order = sellOrderMapper.getReviewDetailById(orderId);
        if (order == null) {
            return null;
        }
        List<String> queryOptions = Arrays.asList(
                OptionsType.TEMPER_CONTROL.name(),
                OptionsType.SHIP_TOOL.name(),
                OptionsType.PAY_METHOD.name(),
                OptionsType.SHIP_METHOD.name());
        List<Options> options = optionsMapper.findByTypes(order.getCompanyId(), queryOptions);
        if (options != null && !options.isEmpty()) {
            Map<Integer, Options> optionsMap = new HashMap<>();
            options.stream().forEach(item -> optionsMap.put(Long.valueOf(item.getId()).intValue(), item));
            Options options1 = optionsMap.get(order.getTemperControlId());
            if (options1 != null) {
                order.setTemperControlName(options1.getValue());
            }
            Options options2 = optionsMap.get(order.getPayMethod());
            if (options2 != null) {
                order.setPayMethodName(options2.getValue());
            }
            Options options3 = optionsMap.get(order.getShipMethod());
            if (options3 != null) {
                order.setShipMethodName(options3.getValue());
            }
            Options options4 = optionsMap.get(order.getShipTool());
            if (options4 != null) {
                order.setShipToolName(options4.getValue());
            }
        }
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

    public SellOrderShip saveOrderShip(User user, SellOrderShip sellOrderShip) throws BizException  {
        SellOrderShip reqShip = UtilTool.trimString(sellOrderShip);
        if (reqShip == null || reqShip.getSellOrderId() == null ||reqShip.getShipCompanyId() == null) {
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
        }else {
            reqShip.setCreateBy(user.getNickname());
            reqShip.setCreateTime(new Date());
            sellOrderShipMapper.insertSelective(reqShip);
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

}
