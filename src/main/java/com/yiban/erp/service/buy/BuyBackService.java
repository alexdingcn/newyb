package com.yiban.erp.service.buy;

import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.constant.RepertoryInStatus;
import com.yiban.erp.constant.RepertoryRefType;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.BuyBackAddRequest;
import com.yiban.erp.dto.BuyBackReq;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.goods.GoodsService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BuyBackService {

    private static final Logger logger = LoggerFactory.getLogger(BuyBackService.class);

    @Autowired
    private RepertoryInBackMapper repertoryInBackMapper;
    @Autowired
    private RepertoryInBackDetailMapper repertoryInBackDetailMapper;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;

    @Transactional
    public void add(BuyBackAddRequest addRequest, User user) throws BizException {
        if (addRequest == null || addRequest.getWarehouseId() == null ||
                addRequest.getSupplierId() == null || addRequest.getDetails() == null || addRequest.getDetails().isEmpty()) {
            logger.warn("buy back apply add request params have null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }

        //检查detail中对应的一些数据是否完整
        List<RepertoryInBackDetail> details = addRequest.getDetails();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (RepertoryInBackDetail detail : details) {
            if (!addRequest.getSupplierId().equals(detail.getSupplierId())) {
                throw new BizException(ErrorCode.BUY_BACK_SUPPLIER_ERROR);
            }
            if (detail.getBackQuantity() == null || BigDecimal.ZERO.compareTo(detail.getBackQuantity()) >= 0) {
                throw new BizException(ErrorCode.BUY_BACK_QUANTITY_ERROR);
            }
            if (detail.getBuyPrice() == null || BigDecimal.ZERO.compareTo(detail.getBuyPrice()) > 0) {
                throw new BizException(ErrorCode.BUY_BACK_PRICE_ERROR);
            }
            totalAmount = totalAmount.add(detail.getAmount() == null ? BigDecimal.ZERO : detail.getAmount());
            totalQuantity = totalQuantity.add(detail.getBackQuantity());
        }

        //创建一个退出入库单
        RepertoryInBack repertoryIn = new RepertoryInBack();
        repertoryIn.setCompanyId(user.getCompanyId());
        repertoryIn.setRefType(RepertoryRefType.BUY_BACK.name());
        repertoryIn.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.BUY_BACK));
        repertoryIn.setSupplierId(addRequest.getSupplierId());
        repertoryIn.setSupplierContactId(addRequest.getSupplierContactId());
        repertoryIn.setBuyerId(addRequest.getBuyerId());
        repertoryIn.setStatus(RepertoryInStatus.BACK_INIT.name());
        repertoryIn.setKeyWord(addRequest.getKeyWord());
        repertoryIn.setBackTime(addRequest.getBackTime());
        repertoryIn.setTotalAmount(totalAmount);
        repertoryIn.setTotalQuantity(totalQuantity);
        repertoryIn.setWarehouseId(addRequest.getWarehouseId());
        repertoryIn.setCreatedBy(user.getNickname());
        repertoryIn.setCreatedTime(new Date());
        repertoryInBackMapper.insert(repertoryIn);
        if (repertoryIn.getId() == null || repertoryIn.getId() <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("user:{} add repertoryIn:{} success.", user.getId(), repertoryIn.getId());
        //创建详情信息和修改出库的在单数量
        List<RepertoryInBackDetail> inDetails = makeDetailsForBackApply(repertoryIn, details);
        int count = repertoryInBackDetailMapper.insertBatch(inDetails);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("insert repertoryIn details success.");
        for (RepertoryInBackDetail item : details) {
            repertoryInfoMapper.updateOnWayQuantity(item.getRepertoryInfoId());
        }
    }

    private List<RepertoryInBackDetail> makeDetailsForBackApply(RepertoryInBack repertoryIn, List<RepertoryInBackDetail> details) {
        List<RepertoryInBackDetail> inDetails = new ArrayList<>();
        for (RepertoryInBackDetail info : details) {
            RepertoryInBackDetail item  = new RepertoryInBackDetail();
            item.setInBackId(repertoryIn.getId());
            item.setGoodsId(info.getGoodsId());
            item.setRepertoryInfoId(info.getRepertoryInfoId());
            item.setBackQuantity(info.getBackQuantity());
            item.setBuyPrice(info.getBuyPrice());
            item.setExpDate(info.getExpDate());
            item.setProductDate(info.getProductDate());
            item.setBatchCode(info.getBatchCode());
            item.setAmount(info.getAmount());
            item.setTaxRate(info.getTaxRate());
            item.setSaleState(info.getSaleState());
            item.setCheckStatus(false);
            item.setCreatedBy(repertoryIn.getCreatedBy());
            item.setCreatedTime(new Date());
            inDetails.add(item);
        }
        return inDetails;
    }


    public List<RepertoryInBack> getList(BuyBackReq buyBackReq) {
        List<RepertoryInBack> backs = repertoryInBackMapper.getList(buyBackReq);
        return backs;
    }

    public List<RepertoryInBackDetail> getDetailByBackId(Long backId) {
        List<RepertoryInBackDetail> details = repertoryInBackDetailMapper.getDetailByBackId(backId);
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> goodsIdList = new ArrayList<>();
        details.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
        List<Goods> goodsList = goodsService.getGoodsById(goodsIdList);
        Map<Long, Goods> goodsMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodsMap.put(item.getId(), item));
        details.stream().forEach(item -> item.setGoods(goodsMap.get(item.getGoodsId())));
        return details;
    }


    public void check(BuyBackReq buyBackReq, User user) throws BizException {
        if (buyBackReq == null || buyBackReq.getBackId() == null || StringUtils.isEmpty(buyBackReq.getType())) {
            logger.warn("back order check but params is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        RepertoryInStatus status = RepertoryInStatus.getByName(buyBackReq.getType());
        if (status == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        switch (status) {
            case BACK_BUY_CHECK:
                doBackBuyCheck(buyBackReq, user);
                break;
            case BACK_QUALITY_CHECK:
                doBackQualityCheck(buyBackReq, user);
                break;
            case BACK_QUALITY_RECHECK:
                doBackQualityRecheck(buyBackReq, user);
                break;
            case BACK_FINAL_CHECK:
                doBackFinalCheck(buyBackReq, user);
                break;
             default:
                 throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
    }

    private void doBackBuyCheck(BuyBackReq buyBackReq, User user) throws BizException {
        //采购经理的审核
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(buyBackReq.getBackId());
        if (inBack == null) {
            logger.warn("get buy back order fail. backId:{}", buyBackReq.getBackId());
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        if (!RepertoryInStatus.BACK_INIT.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("buy back order status is not BACK_INIT");
            throw new BizException(ErrorCode.BUY_BACK_ORDER_STATUS_CANNOT_CHECK);
        }
        inBack.setBackBuyUser(user.getNickname());
        inBack.setBackBuyTime(new Date());
        inBack.setBackBuyResult(buyBackReq.getCheckResult());
        inBack.setStatus(RepertoryInStatus.BACK_BUY_CHECK.name());
        inBack.setUpdatedBy(user.getNickname());
        inBack.setUpdatedTime(new Date());
        repertoryInBackMapper.updateByPrimaryKeySelective(inBack);
    }

    private void doBackQualityCheck(BuyBackReq buyBackReq, User user) throws BizException {
        //质管经理的审核
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(buyBackReq.getBackId());
        if (inBack == null) {
            logger.warn("get buy back order fail. backId:{}", buyBackReq.getBackId());
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        if (!RepertoryInStatus.BACK_BUY_CHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("buy back order status is not BACK_BUY_CHECK");
            throw new BizException(ErrorCode.BUY_BACK_ORDER_STATUS_CANNOT_CHECK);
        }
        inBack.setBackQualityUser(user.getNickname());
        inBack.setBackQualityTime(new Date());
        inBack.setBackQualityResult(buyBackReq.getCheckResult());
        inBack.setStatus(RepertoryInStatus.BACK_QUALITY_CHECK.name());
        inBack.setUpdatedBy(user.getNickname());
        inBack.setUpdatedTime(new Date());
        repertoryInBackMapper.updateByPrimaryKeySelective(inBack);
    }

    @Transactional
    public void doBackQualityRecheck(BuyBackReq buyBackReq, User user) throws BizException {
        //质量复核
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(buyBackReq.getBackId());
        if (inBack == null) {
            logger.warn("get buy back order fail. backId:{}", buyBackReq.getBackId());
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        if (!RepertoryInStatus.BACK_QUALITY_CHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("buy back order status is not BACK_QUALITY_CHECK");
            throw new BizException(ErrorCode.BUY_BACK_ORDER_STATUS_CANNOT_CHECK);
        }
        buyBackReq.setDetailCheckStatus(true);
        buyBackReq.setUpdatedBy(user.getNickname());
        buyBackReq.setUpdatedTime(new Date());
        int count = repertoryInBackDetailMapper.updateCheckStatus(buyBackReq);
        if (count <= 0) {
            logger.warn("update detail check status fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        inBack.setStatus(RepertoryInStatus.BACK_QUALITY_RECHECK.name());
        inBack.setUpdatedTime(new Date());
        inBack.setUpdatedBy(user.getNickname());
        inBack.setQualityCheckTime(new Date());
        inBack.setQualityCheckUser(user.getNickname());
        repertoryInBackMapper.updateByPrimaryKeySelective(inBack);
    }

    @Transactional
    public void doBackFinalCheck(BuyBackReq buyBackReq, User user) throws BizException {
        //质管经理的审核
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(buyBackReq.getBackId());
        if (inBack == null) {
            logger.warn("get buy back order fail. backId:{}", buyBackReq.getBackId());
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        if (!RepertoryInStatus.BACK_QUALITY_RECHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("buy back order status is not BACK_QUALITY_RECHECK");
            throw new BizException(ErrorCode.BUY_BACK_ORDER_STATUS_CANNOT_CHECK);
        }
        //修改库存存量信息
        int count = repertoryInfoMapper.buyBackConsumeQuantity(inBack.getId(), user.getNickname(), new Date());
        if (count <= 0) {
            logger.warn("update repertory info quantity fail by inBackId:{}", inBack.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        logger.info("update repertory info quantity success by inBackId:{}", inBack.getId());

        //修改状态
        inBack.setStatus(RepertoryInStatus.BACK_FINAL_CHECK.name());
        inBack.setUpdatedBy(user.getNickname());
        inBack.setUpdatedTime(new Date());
        inBack.setFinalCheckTime(new Date());
        inBack.setFinalCheckUser(user.getNickname());
        repertoryInBackMapper.updateByPrimaryKeySelective(inBack);

        //发布一个事件，用户后续处理(如生成财务账)
        rabbitmqQueueConfig.sendMessage("BuyBackService", RabbitmqQueueConfig.ORDER_BUY_BACK, inBack);
    }

    @Transactional
    public void checkCancel(Long backId, User user) throws BizException {
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(backId);
        if (inBack == null) {
            logger.warn("get in back order fail by inBack:{}", backId);
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        //如果当前状态不是质量复核通过的，不能进行质量复核取消
        if (!RepertoryInStatus.BACK_QUALITY_RECHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("in back order status is not BACK_QUALITY_RECHECK, can not cancel check. backId:{}", backId);
            throw new BizException(ErrorCode.BUY_BACK_CHECK_CANCEL_ERROR);
        }
        int count = repertoryInBackDetailMapper.cancelCheckStatus(backId, user.getNickname(), new Date());
        if (count <= 0) {
            logger.warn("update detail check status fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        inBack.setStatus(RepertoryInStatus.BACK_QUALITY_CHECK.name());
        inBack.setUpdatedTime(new Date());
        inBack.setUpdatedBy(user.getNickname());
        inBack.setQualityCheckTime(null);
        inBack.setQualityCheckUser(null);
        repertoryInBackMapper.updateByPrimaryKeySelective(inBack);
    }


    @Transactional
    public void removeBackOrder(Long backId, User user) throws BizException {
        if (backId == null) {
            logger.warn("backId is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(backId);
        if (inBack == null || !inBack.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get in back order fail by id {} or company error. user:{}", backId, user.getId());
            throw new BizException(ErrorCode.ACCESS_PERMISSION);
        }

        //释放库存中的onWayQuantity
        int count = repertoryInfoMapper.buyBackReleaseOnWayQuantity(inBack.getId());
        if (count <= 0) {
            logger.warn("release repertory info on way quantity fail by in back id:{}", inBack.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        logger.info("release repertory info on way quantity success by in back id:{}", inBack.getId());
        //修改状态
        inBack.setStatus(RepertoryInStatus.DELETE.name());
        inBack.setUpdatedBy(user.getNickname());
        inBack.setUpdatedTime(new Date());
        repertoryInBackMapper.updateByPrimaryKeySelective(inBack);
    }
}
