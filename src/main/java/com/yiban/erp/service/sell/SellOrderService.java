package com.yiban.erp.service.sell;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.SellOrderStatus;
import com.yiban.erp.constant.SellReviewType;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.SellReviewAction;
import com.yiban.erp.dto.SellReviewOrderQuery;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.warehouse.RepertoryService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private SellReviewOptionMapper sellReviewOptionMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    public List<SellOrder> getList(Integer companyId, Integer customerId, Integer salerId,
                                            String refNo, String status, Date createOrderDate, Integer page, Integer size) {
        int limit = (size == null || size <= 0) ? 10 : size;
        int offset = (page == null || page <= 0 ? 0 : page - 1) * limit;
        return sellOrderMapper.getList(companyId, customerId, salerId, refNo, status,createOrderDate, limit, offset);
    }

    public SellOrder orderAdd(User user, SellOrder sellOrder) throws BizException{
        SellOrder reqOrder = UtilTool.trimString(sellOrder);
        reqOrder.setCompanyId(user.getCompanyId());
        if (!validateSellOrder(reqOrder)) {
            logger.warn("user:{} add order but reqOrder can not validate.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_PARAM_ERROR);
        }
        String orderNumber = getOrderNumber(user);
        logger.info("begin create an sell order by user:{} orderNumber:{}", user.getId(), orderNumber);
        reqOrder.setOrderNumber(orderNumber);
        reqOrder.setStatus(SellOrderStatus.INIT.name());
        reqOrder.setCreateBy(user.getNickname());
        reqOrder.setCreateTime(new Date());

        int count = sellOrderMapper.insertSelective(reqOrder);
        if (count > 0 && reqOrder.getId() > 0) {
            logger.info("user:{} success create add sell order:{}", user.getId(), reqOrder.getId());
            return reqOrder;
        }else {
            logger.warn("orderNumber:{} sell order insert database fail.", orderNumber);
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    /**
     * 生成一个销售订单号, 格式如下
     * 'S' + companyId + yyyyMMddHHmmss + 4位随机数字
     * @param user
     * @return
     */
    private String getOrderNumber(User user) {
        StringBuilder orderNo = new StringBuilder("S");
        orderNo.append(user.getCompanyId());
        orderNo.append(UtilTool.DateFormat(new Date(), "yyyyMMddHHmmss"));
        orderNo.append(RandomStringUtils.randomNumeric(4));
        return orderNo.toString();
    }

    public SellOrder orderUpdate(User user, SellOrder sellOrder) throws BizException {
        SellOrder reqOrder = UtilTool.trimString(sellOrder);
        if (!validateSellOrder(reqOrder)) {
            logger.warn("user:{} request update sell order but params error.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_PARAM_ERROR);
        }
        if (reqOrder.getId() == null) {
            logger.warn("user:{} update sell order by id is null.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_PARAM_ERROR);
        }
        logger.info("user:{} begin update sell order:{}", user.getId(), reqOrder.getId());
        reqOrder.setUpdateBy(user.getNickname());
        reqOrder.setUpdateTime(new Date());
        reqOrder.setStatus(null); //当前修改方法不对状态进行修改
        int count = sellOrderMapper.updateByPrimaryKeySelective(reqOrder);
        if (count > 0) {
            logger.info("user:{} update sell order:{} success.", user.getId(), reqOrder.getId());
            return reqOrder;
        }else {
            logger.warn("user:{} sell order:{} update database fail.", user.getId(), reqOrder.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
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
        if (order.getSalerId() == null) {
            logger.warn("saler id is null.");
            return false;
        }
        return true;
    }

    public int removeSellOrder(User user, Long id) throws BizException {
        logger.info("user:{} remove sell order id:{}, set to delete status.", user.getId(), id);
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
            if (repertoryInfo == null || repertoryInfo.getGoodId() == null) {
                continue;
            }
            Long goodId = repertoryInfo.getGoodId();
            if (!result.containsKey(goodId)) {
                List<SellOrderDetail> itemList = new ArrayList<>();
                result.put(goodId, itemList);
            }
            result.get(goodId).add(detail);
        }
        return result;
    }

    public int detailSave(final User user, final List<SellOrderDetail> details) throws BizException {
        int count = 0;
        //获取订单信息，去第一个
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(details.get(0).getSellOrderId());
        if (sellOrder == null || !SellOrderStatus.INIT.name().equalsIgnoreCase(sellOrder.getStatus())) {
            logger.warn("user:{} save sell order detail but sell order is not init status. sell id:{}",
                    user.getId(), details.get(0).getSellOrderId());
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_CAN_NOT_UPDATE);
        }
        //验证客户是否允许经营特殊管理药品
        List<Long> goodIds = new ArrayList<>();
        details.stream().forEach(item -> goodIds.add(item.getGoodId()));
        if(!isCustomerCanSellGoods(sellOrder.getCustomerId(), goodIds)) {
            logger.warn("user: {} save sell order detail good have special managed but customer:{} can not shell.", user.getId(), sellOrder.getCustomerId());
            throw new BizException(ErrorCode.SELL_ORDER_CUSTOMER_CANNOT_SELL_GOOD);
        }
        for (SellOrderDetail detail : details) {
            try {
                int result = saveOneDetail(user, detail);
                if (result > 0) {
                    logger.info("user:{} success save one sell order detail record.", user.getId());
                    count ++; //保存成功的笔数
                }
            }catch (Exception e) {
                logger.error("user:{} save sell order detail have exception", user.getId(), e);
            }
        }
        return count;
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

    private int saveOneDetail(User user, SellOrderDetail detail) {
        if (detail == null) {
            return -1;
        }
        Long repertoryId = detail.getRepertoryId();
        Long orderId = detail.getSellOrderId();
        if (repertoryId == null) {
            logger.warn("user:{} save sell order detail but good id is null.", user.getId());
            return -1;
        }
        if (orderId == null) {
            logger.warn("user:{} save sell order detail but order id is null.", user.getId());
            return -1;
        }
        if (detail.getId() != null && detail.getId() > 0) {
            logger.info("user:{} save sell order detail is update id:{}", user.getId(), detail.getId());
            detail.setUpdateBy(user.getNickname());
            detail.setUpdateTime(new Date());
            return sellOrderDetailMapper.updateByPrimaryKeySelective(detail);
        }else {
            logger.info("user:{} save sell order detail is add", user.getId());
            detail.setCreateBy(user.getNickname());
            detail.setCreateTime(new Date());
            return sellOrderDetailMapper.insertSelective(detail);
        }
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
        if (sellOrder == null || !SellOrderStatus.INIT.name().equalsIgnoreCase(sellOrder.getStatus())) {
            logger.warn("user:{} remove sell order detail but sell order is null or status is not init. id:{}", user.getId(), detailId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_CAN_NOT_REMOVE);
        }
        logger.info("user:{} request to remove sell order detail by id:{}", user.getId(), detailId);
        sellReviewOptionMapper.deleteByDetailIdList(Arrays.asList(detailId), null); // 如果有审批意见的话，先删除
        return sellOrderDetailMapper.deleteByPrimaryKey(detailId);
    }

    public List<SellOrder> getReviewOrderList(SellReviewOrderQuery query) {
        if (query.getStatusList() == null || query.getStatusList().isEmpty()) {
            query.setStatusList(null);
        }
        return sellOrderMapper.getReviewOrderList(query);
    }

    public void reviewOrderOk(User user, SellReviewAction reviewAction) throws BizException {
        if (reviewAction == null || StringUtils.isBlank(reviewAction.getReviewType())) {
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
        List<SellReviewOption> options = sellReviewOptionMapper.getByDetailIdList(detailIdList, reviewAction.getReviewType());
        Map<Long, SellReviewOption> oldOptionMap = new HashMap<>();
        if (options != null && !options.isEmpty()) {
            options.stream().forEach(item -> oldOptionMap.put(item.getSellDetailId(), item));
        }
        List<SellReviewOption> newReviewRecordList = new ArrayList<>();
        for (Long detailId : detailIdList) {
            if (oldOptionMap.containsKey(detailId)) {
                SellReviewOption option = oldOptionMap.get(detailId);
                option.setReviewStatus("OK");
                option.setReviewComment(reviewAction.getReviewComment());
                option.setUpdateBy(user.getNickname());
                option.setUpdateTime(new Date());
                newReviewRecordList.add(option);
            }else {
                SellReviewOption option = new SellReviewOption();
                option.setSellDetailId(detailId);
                option.setReviewType(reviewAction.getReviewType());
                option.setReviewStatus("OK");
                option.setReviewComment(reviewAction.getReviewComment());
                option.setCreateBy(user.getNickname());
                option.setCreateTime(new Date());
                option.setUpdateBy(user.getNickname());
                option.setUpdateTime(new Date());
                newReviewRecordList.add(option);
            }
        }
        sellReviewOptionMapper.replace(newReviewRecordList);
        List<Long> uncheckIds = sellReviewOptionMapper.getUnCheckDetailIdList(sellOrderId, reviewAction.getReviewType());
        if (!uncheckIds.isEmpty()) {
            logger.debug("have uncheck detail: {}", JSON.toJSONString(uncheckIds));
            return;
        }
        //如果完成了，相应修改订单的状态
        if (SellReviewType.QUALITY_REVIEW.name().equalsIgnoreCase(reviewAction.getReviewType())) {
            //出库质量审核
            logger.info("sell order:{} have check quality review ok", sellOrderId);
            sellOrder.setStatus(SellOrderStatus.QUALITY_CHECKED.name());
            sellOrder.setUpdateBy(user.getNickname());
            sellOrder.setUpdateTime(new Date());
            sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
        }else if (SellReviewType.SALE_REVIEW.name().equalsIgnoreCase(reviewAction.getReviewType())){
            //销售审核
            logger.info("sell order:{} have check sale review ok", sellOrderId);
            sellOrder.setStatus(SellOrderStatus.SALE_CHECKED.name());
            sellOrder.setUpdateBy(user.getNickname());
            sellOrder.setUpdateTime(new Date());
            sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
        }

        //TODO 如果是销售审核通过了，需要对库存进行减去
    }

    public int reviewCancel(User user, SellReviewAction action) throws BizException {
        if (action == null || action.getDetailList() == null || StringUtils.isBlank(action.getReviewType())) {
            logger.warn("user:{} review cancel but params error.", user.getId());
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_SUBMIT_PARAMS);
        }
        Long sellOrderId = action.getSellOrderId();
        SellOrder sellOrder = sellOrderMapper.selectByPrimaryKey(sellOrderId);
        if (sellOrder == null) {
            logger.warn("user:{} review cancel but sell order not found by id:{}", user.getId(), sellOrderId);
            throw new BizException(ErrorCode.SELL_ORDER_DETAIL_GET_FAIL);
        }
        //直接根据detailId和type进行删除审批记录信息
        List<Long> detailIdList = action.getDetailIdList();
        int count = sellReviewOptionMapper.deleteByDetailIdList(detailIdList, action.getReviewType());
        //如果order的状态是已经审批完成的，改回上一级状态
        if (SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())) {
            sellOrder.setStatus(SellOrderStatus.INIT.name());
            sellOrderMapper.updateByPrimaryKeySelective(sellOrder);
        }else if (SellOrderStatus.SALE_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())) {
            sellOrder.setStatus(SellOrderStatus.QUALITY_CHECKED.name());
            sellOrderMapper.updateByPrimaryKeySelective(sellOrder);

            //TODO 如果是销售审核撤销了，需要对库存量加回去
        }
        return count;
    }

    public SellOrder reviewDetail(Long orderId) {
        SellOrder order = sellOrderMapper.getReviewDetailById(orderId);
        if (order == null) {
            return null;
        }
        List<SellOrderDetail> details = getDetailList(orderId);
        //设置每一笔销售产品的审批意见信息
        setSellOrderDetailReviewOptions(details);
        order.setDetails(details);
        return order;
    }

    public void setSellOrderDetailReviewOptions(List<SellOrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        List<Long> detailIdList = new ArrayList<>();
        details.stream().forEach(item -> detailIdList.add(item.getId()));
        List<SellReviewOption> options = sellReviewOptionMapper.getByDetailIdList(detailIdList, null);
        if (options != null && !options.isEmpty()) {
            Map<Long, List<SellReviewOption>> optionMap = options.stream()
                    .collect(Collectors.groupingBy(SellReviewOption::getSellDetailId));
            details.stream().forEach(item -> item.setReviewOptions(optionMap.get(item.getId())));
        }
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
