package com.yiban.erp.service.sell;

import com.yiban.erp.constant.SellOrderStatus;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.SellOrderDetailMapper;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.SellOrderDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
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

    public List<SellOrderDetail> getDetailList(Long sellOrderId) {
        if (sellOrderId == null) {
            logger.warn("get sell order detail by sell order id is null.");
            return Collections.emptyList();
        }
        List<SellOrderDetail> details = sellOrderDetailMapper.getDetailList(sellOrderId);
        //如果存在，把对应的产品信息查询出来关联到对应的订单详情中
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> goodsIdList = new ArrayList<>();
        details.stream().forEach(item -> goodsIdList.add(item.getGoodId()));
        List<Goods> goods = goodsMapper.selectByIdList(goodsIdList);
        Map<Long, List<Goods>> map = goods.stream().collect(Collectors.groupingBy(Goods::getId));
        details.stream().forEach(item -> {
            List<Goods> tempList = map.get(item.getGoodId());
            Goods goodsItem =  (tempList == null ? null : tempList.get(0));
            item.setGoods(goodsItem);
            item.setGoodName(goodsItem.getName());
        });

        return details;
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

    private int saveOneDetail(User user, SellOrderDetail detail) {
        if (detail == null) {
            return -1;
        }
        Long goodId = detail.getGoodId();
        Long orderId = detail.getSellOrderId();
        if (goodId == null) {
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
        return sellOrderDetailMapper.deleteByPrimaryKey(detailId);
    }


    public List<SellOrder> getReviewList(Integer companyId, String reviewType, String orderNumber,
                                         Integer salerId, Date startDate, Date endDate) throws BizException{
        Map<String, Object> map = new HashMap<>();
        map.put("companyId", companyId);
        map.put("status", reviewType);
        map.put("orderNumber", orderNumber);
        map.put("salerId", salerId);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        return sellOrderMapper.getReviewList(map);
    }

    public void submitOrderReview(User user, String reviewType, List<Long> idList) throws BizException {
        if (StringUtils.isBlank(reviewType) || idList == null || idList.isEmpty()) {
            logger.warn("submit order review info but params is null.");
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_SUBMIT_PARAMS);
        }
        if (!SellOrderStatus.INIT.name().equalsIgnoreCase(reviewType)
                && !SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(reviewType)) {
            logger.error("sell order review type error. userId:{}, reviewType:{}", user.getId(), reviewType);
            //审核流程错误
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_STATUS_ERROR);
        }
        List<SellOrder> orders = sellOrderMapper.getListById(idList);
        if (orders.isEmpty() || orders.size() != idList.size()) {
            logger.warn("submit order review get order by id but size is error.");
            throw new BizException(ErrorCode.SELL_ORDER_REVIEW_SUBMIT_ID_ERROR);
        }
        for (SellOrder order : orders) {
            if (!reviewType.equalsIgnoreCase(order.getStatus())) {
                logger.warn("submit order review but status is error. id:{} reviewType:{}", order.getId(), reviewType);
                throw new BizException(ErrorCode.SELL_ORDER_REVIEW_STATUS_ERROR);
            }
        }
        for (SellOrder order : orders) {
            if (SellOrderStatus.INIT.name().equalsIgnoreCase(order.getStatus())) {
                //出库质量审核
                order.setStatus(SellOrderStatus.QUALITY_CHECKED.name());
                order.setUpdateBy(user.getNickname());
                order.setUpdateTime(new Date());
                sellOrderMapper.updateByPrimaryKeySelective(order);
            }else {
                //销售审核
                order.setStatus(SellOrderStatus.SALE_CHECKED.name());
                order.setUpdateBy(user.getNickname());
                order.setUpdateTime(new Date());
                sellOrderMapper.updateByPrimaryKeySelective(order);
            }
        }
    }

    public SellOrder reviewDetai(Long orderId) {
        SellOrder order = sellOrderMapper.getReviewDetailById(orderId);
        if (order == null) {
            return null;
        }
        List<SellOrderDetail> details = getDetailList(orderId);
        order.setDetails(details);
        return order;
    }


}
