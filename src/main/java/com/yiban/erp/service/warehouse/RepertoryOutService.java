package com.yiban.erp.service.warehouse;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.*;
import com.yiban.erp.dao.*;
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
import java.util.Date;
import java.util.List;


@Service
public class RepertoryOutService {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryOutService.class);

    @Autowired
    private RepertoryOutMapper  repertoryOutMapper;
    @Autowired
    private RepertoryOutDetailMapper  repertoryOutDetailMapper;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;

    //保存移库出库
    @Transactional
    public void saveChangeRepertoryOut(User user, RepertoryOut repertoryOut) throws BizException {

        if (!saveValidate(repertoryOut)) {
            throw new BizException(ErrorCode.REPERTORY_CHANGE_PARAMS_ERROR);
        }
        setRepertoryOutTotalAmount(repertoryOut, repertoryOut.getOutDetailList()); //保存的之前先对订单的总入库数和总金额计算
        if (repertoryOut.getId() == null) {
            //新建
            repertoryOut.setRefType(RepertoryRefType.MOVE_OUT.name()); //如果没有设置，也就是不是采购入库，直接设置为直调入库
            repertoryOut.setCompanyId(user.getCompanyId());
            repertoryOut.setRefOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.IN_CHECK));
            repertoryOut.setCreatedBy(user.getNickname());
            repertoryOut.setCreatedTime(new Date());
            repertoryOut.setMakeOrderUser(user.getNickname());
            int count = repertoryOutMapper.insert(repertoryOut);
            if (count <= 0 || repertoryOut.getId() == null) {
                logger.warn("save repertoryOut insert fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }else {
            RepertoryOut oldOrder = repertoryOutMapper.selectByPrimaryKey(repertoryOut.getId());
            if (oldOrder == null) {
                logger.warn("get repertoryOut order fail by id:{}", repertoryOut.getId());
                throw new BizException(ErrorCode.RECEIVE_ORDER_NOT_FOUND);
            }
            if (oldOrder.getCompanyId() == null || !oldOrder.getCompanyId().equals(user.getCompanyId())) {
                logger.warn("old order company is not match user company. order id:{}, user id:{}", repertoryOut.getId(), user.getId());
                throw new BizException(ErrorCode.ACCESS_PERMISSION);
            }
            oldOrder.setUpdatedBy(user.getNickname());
            oldOrder.setUpdatedTime(new Date());
            int count = repertoryOutMapper.updateByPrimaryKeySelective(oldOrder);
            if (count <= 0) {
                logger.warn("save order update fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }
        logger.info("begin save repository order details.");
        saveOrderDetail(user, repertoryOut);
    }

    private int saveOrderDetail(User user, RepertoryOut order) {
        List<RepertoryOutDetail> details = order.getOutDetailList();
        //直接删除原有的，然后重新插入数据
        try{
            repertoryOutDetailMapper.deleteByRepertoryOutId(order.getId());
        }catch(Exception e){
        }

        details.stream().forEach(item -> {
            Long rInfoId=item.getRepertoryInfoId();
            RepertoryInfo rinfo=repertoryInfoMapper.selectByPrimaryKey(rInfoId);
            item.setRepertoryInfoId(rinfo.getId());
            item.setBatchCode(rinfo.getBatchCode());
            item.setProductDate(rinfo.getProductDate());
            item.setExpDate(rinfo.getExpDate());
            item.setGoodsId(rinfo.getGoodsId());
            item.setPrice(rinfo.getBuyPrice());
            item.setTaxRate(rinfo.getTaxRate());
            item.setRepertoryOutId(order.getId());
            item.setCreatedBy(user.getNickname());
            item.setCreatedTime(new Date());
            item.setAmount(item.getQuantity().multiply(rinfo.getBuyPrice()));
        });
        return repertoryOutDetailMapper.insertBatch(details);
    }


    private boolean saveValidate(RepertoryOut order) {
        if (order == null) {
            logger.warn("save order is null");
            return false;
        }
        if (order.getOutDetailList() == null) {
            logger.warn("save order but OutDetailList  is null.");
            return false;
        }
        if (order.getWarehouseId() == null) {
            logger.warn("save order but warehouse id is null.");
            return false;
        }
        return true;
    }
    /**
     * 注意order 和 details 的匹配
     * @param order
     * @param details
     */
    private void setRepertoryOutTotalAmount(RepertoryOut order, List<RepertoryOutDetail> details) {
        if (order == null || details == null || details.isEmpty()) {
            return;
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (RepertoryOutDetail detail : details) {
            totalQuantity = totalQuantity.add(detail.getQuantity()); //金额
        }
        order.setTotalQuantity(totalQuantity);
    }

}
