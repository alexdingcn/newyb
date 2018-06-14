package com.yiban.erp.service.buy;

import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.constant.ConfigKey;
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
import com.yiban.erp.service.util.SystemConfigService;
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
    @Autowired
    private SystemConfigService systemConfigService;

    private RepertoryInStatus getSaveStatusByConfigFlow(Integer companyId) {
        // 获取系统配置的入库流程
        Map<String, SystemConfig> configMap = systemConfigService.getConfigMap(companyId);
        SystemConfig bmFlow = configMap.get(ConfigKey.BUY_BACK_BM_CHECK.name());
        if (bmFlow != null && "open".equalsIgnoreCase(bmFlow.getKeyValue())) {
            return RepertoryInStatus.BACK_INIT;
        }
        SystemConfig qmFlow = configMap.get(ConfigKey.BUY_BACK_QA_CHECK.name());
        if (qmFlow != null && "open".equalsIgnoreCase(qmFlow.getKeyValue())) {
            return RepertoryInStatus.BACK_BUY_CHECK;
        }
        SystemConfig reQaFlow = configMap.get(ConfigKey.BUY_BACK_QUALITY_CHECK.name());
        if (reQaFlow != null && "open".equalsIgnoreCase(reQaFlow.getKeyValue())) {
            return RepertoryInStatus.BACK_QUALITY_CHECK;
        }
        return RepertoryInStatus.BACK_QUALITY_RECHECK; //如果都没有配置，直接返回待终审状态
    }

    @Transactional
    public void save(RepertoryInBack inBack, User user) throws BizException {
        if (inBack == null || inBack.getInOrderId() == null || inBack.getDetails() == null || inBack.getDetails().isEmpty()) {
            logger.warn("buy back apply add request params have null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }

        //检查detail中对应的一些数据是否完整
        List<RepertoryInBackDetail> details = inBack.getDetails();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (RepertoryInBackDetail detail : details) {
            if (detail.getBackQuantity() == null || BigDecimal.ZERO.compareTo(detail.getBackQuantity()) > 0) {
                throw new BizException(ErrorCode.BUY_BACK_QUANTITY_ERROR);
            }
            totalAmount = totalAmount.add(detail.getAmount() == null ? BigDecimal.ZERO : detail.getAmount());
            totalQuantity = totalQuantity.add(detail.getBackQuantity());
        }
        inBack.setTotalAmount(totalAmount);
        inBack.setTotalQuantity(totalQuantity);
        //根据系统配置流程，设置对应的状态
        inBack.setStatus(getSaveStatusByConfigFlow(user.getCompanyId()).name());

        //判断订单是否有ID，如果有，认为是修改，如果没有，认为是新增
        if (inBack.getId() == null) {
            logger.info("user:{} add repertory in back order.", user.getId());
            addInBackOrder(inBack, user);
        }else {
            RepertoryInBack old = repertoryInBackMapper.selectByPrimaryKey(inBack.getId());
            if (old == null || !old.getCompanyId().equals(user.getCompanyId())) {
                logger.warn("get repertory in order fail by id:{}", inBack.getId());
                throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
            }
            logger.info("user:{} update repertory in back order:{}", user.getId(), inBack.getId());
            updateInBackOrder(inBack, user);
        }
    }

    @Transactional
    public void addInBackOrder(RepertoryInBack inBack, User user) {
        inBack.setCompanyId(user.getCompanyId());
        inBack.setRefType(RepertoryRefType.BUY_BACK.name());
        inBack.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.BUY_BACK));
        if (inBack.getBackTime() == null) {
            inBack.setBackTime(new Date());
        }
        inBack.setCreatedBy(user.getNickname());
        inBack.setCreatedTime(new Date());
        int count = repertoryInBackMapper.insert(inBack);
        if (count <=0 || inBack.getId() == null) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        //直接插入对应的详情信息
        List<RepertoryInBackDetail> inDetails = makeDetailsForBackApply(inBack, inBack.getDetails());
        int detailCount = repertoryInBackDetailMapper.insertBatch(inDetails);
        if (detailCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("insert repertoryIn details success.");
        for (RepertoryInBackDetail item : inDetails) {
            repertoryInfoMapper.updateOnWayQuantity(item.getRepertoryInfoId());
        }
    }

    @Transactional
    public void updateInBackOrder(RepertoryInBack inBack, User user) {
        inBack.setUpdatedTime(new Date());
        inBack.setUpdatedBy(user.getNickname());
        int count = repertoryInBackMapper.updateByPrimaryKeySelective(inBack);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        //直接把原来有所有明细删除，然后加入新的
        repertoryInBackDetailMapper.deleteByInBackId(inBack.getId());
        List<RepertoryInBackDetail> inDetails = makeDetailsForBackApply(inBack, inBack.getDetails());
        int detailCount = repertoryInBackDetailMapper.insertBatch(inDetails);
        if (detailCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("insert repertoryIn details success.");
        for (RepertoryInBackDetail item : inDetails) {
            repertoryInfoMapper.updateOnWayQuantity(item.getRepertoryInfoId());
        }
    }

    private List<RepertoryInBackDetail> makeDetailsForBackApply(RepertoryInBack repertoryIn, List<RepertoryInBackDetail> details) {
        for (RepertoryInBackDetail info : details) {
            info.setInBackId(repertoryIn.getId());
            info.setCheckStatus(false);
            info.setCreatedBy(repertoryIn.getCreatedBy());
            info.setCreatedTime(new Date());
        }
        return details;
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
        if (inBack == null || !inBack.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get buy back order fail. backId:{}", buyBackReq.getBackId());
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        //不校验当前状态，任何时候都可以做数据登记，但是是否修改状态，需要根据当前状态和系统流程配置来决定变更状态
        String status = RepertoryInStatus.BACK_BUY_CHECK.name();
        if (RepertoryInStatus.BACK_INIT.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.info("in back order status is BACK_INIT.");
            //需要考虑下一步的流程
            Map<String, SystemConfig> configMap = systemConfigService.getConfigMap(user.getCompanyId());
            //如果有质管审批
            SystemConfig qmFlow = configMap.get(ConfigKey.BUY_BACK_QA_CHECK.name());
            SystemConfig qaFlow = configMap.get(ConfigKey.BUY_BACK_QUALITY_CHECK);
            if (qmFlow != null && "open".equalsIgnoreCase(qmFlow.getKeyValue())) {
                status = RepertoryInStatus.BACK_BUY_CHECK.name();
            }else if (qaFlow != null && "open".equalsIgnoreCase(qaFlow.getKeyValue())) {
                status = RepertoryInStatus.BACK_QUALITY_CHECK.name(); //下一步直接到质量验证
            }else {
                //直接到终审
                status = RepertoryInStatus.BACK_QUALITY_RECHECK.name();
            }
        }else {
            //只做信息登记，原来什么状态还是返回什么状态
            status = inBack.getStatus();
        }
        logger.info("inBackOrder: {} update to status:{}", inBack.getId(), status);
        inBack.setBackBuyUser(user.getNickname());
        inBack.setBackBuyTime(new Date());
        inBack.setBackBuyResult(buyBackReq.getCheckResult());
        inBack.setStatus(status);
        inBack.setUpdatedBy(user.getNickname());
        inBack.setUpdatedTime(new Date());
        repertoryInBackMapper.updateByPrimaryKeySelective(inBack);
    }

    private void doBackQualityCheck(BuyBackReq buyBackReq, User user) throws BizException {
        //质管经理的审核
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(buyBackReq.getBackId());
        if (inBack == null || !inBack.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get buy back order fail. backId:{}", buyBackReq.getBackId());
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        String status = RepertoryInStatus.BACK_QUALITY_CHECK.name();
        if (!RepertoryInStatus.BACK_INIT.name().equalsIgnoreCase(inBack.getStatus())
                || !RepertoryInStatus.BACK_BUY_CHECK.name().equalsIgnoreCase(inBack.getStatus())
                || !RepertoryInStatus.BACK_QUALITY_CHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            //除了这三种状态，需要考虑下一步的流程，其他状态下只做登记，
            Map<String, SystemConfig> configMap = systemConfigService.getConfigMap(user.getCompanyId());
            SystemConfig bmFlow = configMap.get(ConfigKey.BUY_BACK_BM_CHECK); //采购经理审核流程
            SystemConfig qmFlow = configMap.get(ConfigKey.BUY_BACK_QA_CHECK); //质管经理审核流程
            SystemConfig qaFlow = configMap.get(ConfigKey.BUY_BACK_QUALITY_CHECK); //质量复核流程
            if (bmFlow != null && "open".equalsIgnoreCase(bmFlow.getKeyValue())
                    && !RepertoryInStatus.BACK_BUY_CHECK.name().equalsIgnoreCase(inBack.getStatus())) {
                //需要先经过采购经理审核这一笔
                throw new BizException(ErrorCode.BUY_BACK_NEED_BUY_CHECK);
            }else if (qmFlow == null || !"open".equalsIgnoreCase(qmFlow.getKeyValue())) {
                //质管经理这步流程不需要操作的，直接做记录登记，原来什么状态还是已什么状态返回
                status = inBack.getStatus();
            }else if (qaFlow != null && "open".equalsIgnoreCase(qaFlow.getKeyValue())) {
                //进入采购退出单质量复核
                status = RepertoryInStatus.BACK_QUALITY_CHECK.name();
            }else {
                //直接进入终审
                status = RepertoryInStatus.BACK_QUALITY_RECHECK.name();
            }
        }else {
            status = inBack.getStatus();
        }
        inBack.setBackQualityUser(user.getNickname());
        inBack.setBackQualityTime(new Date());
        inBack.setBackQualityResult(buyBackReq.getCheckResult());
        inBack.setStatus(status);
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
        //质量复核的流程操作，只有在订单处于质管审核通过的状态下才能操作，其他不允许操作
        if (!RepertoryInStatus.BACK_QUALITY_CHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("buy back order status is BACK_FINAL_CHECK");
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
        //采购退出单终审
        RepertoryInBack inBack = repertoryInBackMapper.selectByPrimaryKey(buyBackReq.getBackId());
        if (inBack == null) {
            logger.warn("get buy back order fail. backId:{}", buyBackReq.getBackId());
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        //只有在订单处于质量复核通过的状态下才能操作，其他不允许操作
        if (!RepertoryInStatus.BACK_QUALITY_RECHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("buy back order status is not BACK_QUALITY_RECHECK");
            throw new BizException(ErrorCode.BUY_BACK_ORDER_STATUS_CANNOT_CHECK);
        }
        //修改库存存量信息
        int count = repertoryInfoMapper.buyBackConsumeQuantity(inBack.getId(), user.getNickname(), new Date());
        if (count <= 0) {
            logger.warn("update repertory info quantity fail by inBackId:{}", inBack.getId());
            throw new BizRuntimeException(ErrorCode.BUY_BACK_UPDATE_REPERTORY_FAIL);
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
        if (RepertoryInStatus.BACK_FINAL_CHECK.name().equalsIgnoreCase(inBack.getStatus())) {
            logger.warn("back order have final check, can not remove. inBackId:{}", backId);
            throw new BizException(ErrorCode.BUY_BACK_STATUS_CANNOT_REMOVE);
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

    public RepertoryInBack getViewOrder(Long id, User user) throws BizException {
        RepertoryInBack back = repertoryInBackMapper.getById(id);
        if (back == null || !user.getCompanyId().equals(back.getCompanyId())) {
            throw new BizException(ErrorCode.BUY_BACK_ORDER_GET_FAIL);
        }
        List<RepertoryInBackDetail> details = getDetailByBackId(id);
        back.setDetails(details);
        return back;
    }
}
