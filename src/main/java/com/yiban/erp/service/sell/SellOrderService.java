package com.yiban.erp.service.sell;

import com.yiban.erp.constant.SellOrderStatus;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SellOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderService.class);

    @Autowired
    private SellOrderMapper sellOrderMapper;

    public List<SellOrder> getList(Integer companyId, Integer customerId, Integer salerId,
                                            String refNo, String status, Integer page, Integer size) {
        int limit = (size == null || size <= 0) ? 10 : size;
        int offset = (page == null || page <= 0 ? 0 : page - 1) * limit;
        return sellOrderMapper.getList(companyId, customerId, salerId, refNo, status, limit, offset);
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


}
