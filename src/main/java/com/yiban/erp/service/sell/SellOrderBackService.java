package com.yiban.erp.service.sell;

import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.constant.SellBackStatus;
import com.yiban.erp.constant.SellOrderStatus;
import com.yiban.erp.dao.SellOrderBackDetailMapper;
import com.yiban.erp.dao.SellOrderBackMapper;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SellOrderBackService {

    private static final Logger logger = LoggerFactory.getLogger(SellOrderBackService.class);

    @Autowired
    private SellOrderBackMapper sellOrderBackMapper;
    @Autowired
    private SellOrderBackDetailMapper sellOrderBackDetailMapper;
    @Autowired
    private SellOrderMapper sellOrderMapper;


    private void validateAddOrder(SellOrderBack back) throws BizException {
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
        if (back.getFreeAmount() != null && BigDecimal.ZERO.compareTo(back.getFreeAmount()) < 0) {
            logger.warn("params costAmount must <= 0");
            throw new BizException(ErrorCode.SELL_BACK_COST_AMOUNT_ERROR);
        }
        List<SellOrderBackDetail> details = back.getDetails();
        if (details == null || details.isEmpty()) {
            logger.warn("params details is empty.");
            throw new BizException(ErrorCode.SELL_BACK_ADD_DETAIL_EMPTY);
        }
    }

    @Transactional
    public void add(SellOrderBack orderBack, User user) throws BizException {
        if (orderBack ==null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        validateAddOrder(orderBack);
        //获取详情退货数量大于0的产品信息
        List<SellOrderBackDetail> details = new ArrayList<>();
        orderBack.getDetails()
            .stream().forEach(item -> {
                if (item.getBackQuantity() != null && item.getBackQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    details.add(item);
                }
            });
        if (details.isEmpty()) {
            logger.warn("params add detail back quantity <= 0");
            throw new BizException(ErrorCode.SELL_BACK_ADD_DETAIL_QUANTITY_ERROR);
        }
        SellOrder sellOrder = null;
        if (orderBack.getHaveSellOrder()) {
            sellOrder = sellOrderMapper.selectByPrimaryKey(orderBack.getRefOrderId());
            if (sellOrder == null || !SellOrderStatus.SALE_CHECKED.name().equalsIgnoreCase(sellOrder.getStatus())) {
                throw new BizException(ErrorCode.SELL_BACK_GET_OUT_RECORD_FAIL);
            }
            //校验退货数量是否超出订单销售数量
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCostAmount = BigDecimal.ZERO;
        for (SellOrderBackDetail detail : details) {
            totalQuantity = totalQuantity.add(detail.getBackQuantity());
            totalAmount = totalAmount.add(detail.getAmount() == null ? BigDecimal.ZERO : detail.getAmount());
            totalCostAmount = totalCostAmount.add(detail.getCostAmount() == null ? BigDecimal.ZERO : detail.getCostAmount());
        }

        //创建一个销售退货单信息
        orderBack.setCompanyId(user.getCompanyId());
        orderBack.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.SELL_BACK));
        orderBack.setStatus(SellBackStatus.INIT.name());
        if (orderBack.getHaveSellOrder()) {
            orderBack.setRefOrderId(sellOrder.getId());
            orderBack.setRefOrderNo(sellOrder.getOrderNumber());
        }
        orderBack.setTotalAmount(totalAmount);
        orderBack.setTotalQuantity(totalQuantity);
        orderBack.setTotalCostAmount(totalCostAmount);
        orderBack.setCreatedBy(user.getNickname());
        orderBack.setCreatedTime(new Date());
        int count = sellOrderBackMapper.insert(orderBack);
        if (count <=0 || orderBack.getId() == null) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("sell back order insert success id:{}", orderBack.getId());

        for (SellOrderBackDetail detail : details) {
            detail.setBackOrderId(orderBack.getId());
            detail.setCreatedBy(user.getNickname());
            detail.setCreatedTime(new Date());
        }

        //保存详情信息
        int detailCount = sellOrderBackDetailMapper.insertBatch(details);
        if (detailCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("sell back order detail insert success. detailCount:{}", detailCount);

        if (orderBack.getHaveSellOrder()) {
            //修改订单详情的退货数量
            sellOrderBackDetailMapper.updateAlreadyBackQuantity(orderBack.getId());
        }
    }

}
