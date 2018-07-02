package com.yiban.erp.service.warehouse;

import com.yiban.erp.constant.*;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.dto.RepertoryOutListReq;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;

import com.yiban.erp.service.goods.GoodsService;
import com.yiban.erp.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


@Service
public class RepertoryOutService {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryOutService.class);
    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private RepertoryOutMapper  repertoryOutMapper;
    @Autowired
    private RepertoryOutDetailMapper  repertoryOutDetailMapper;
    @Autowired
    private RepertoryService repertoryService;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private OptionsMapper optionsMapper;
    @Autowired
    private GoodsService goodsService;

    //保存移库出库
    @Transactional
    public void saveChangeRepertoryOut(User user, RepertoryOut repertoryOut) throws BizException {

        if (!saveValidate(repertoryOut)) {
            throw new BizException(ErrorCode.REPERTORY_CHANGE_PARAMS_ERROR);
        }
        setRepertoryOutTotalAmount(repertoryOut, repertoryOut.getOutDetailList()); //保存的之前先对订单的总入库数和总金额计算
        if (repertoryOut.getId() == null) {
            //新建
            repertoryOut.setRefType(RepertoryRefType.MOVE_OUT.name());
            repertoryOut.setStatus(RepertoryOutStatus.INIT.name());
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
            oldOrder.setReviewOrderResult("");
            oldOrder.setReviewOrderUser("");
            oldOrder.setStatus("INIT");
            oldOrder.setOutDate(repertoryOut.getOutDate());
            oldOrder.setComment(repertoryOut.getComment());
            oldOrder.setGoToWarehouseId(repertoryOut.getGoToWarehouseId());
            oldOrder.setGoTo(repertoryOut.getGoTo());
            oldOrder.setCustomerName(repertoryOut.getCustomerName());
            int count = repertoryOutMapper.updateByPrimaryKeySelective(oldOrder);
            if (count <= 0) {
                logger.warn("save order update fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }
        logger.info("begin save repository order details.");
        saveOrderDetail(user, repertoryOut);
    }

    //损耗出库
    @Transactional
    public void saveLoseRepertoryOut(User user, RepertoryOut repertoryOut) throws BizException {

        if (!saveValidate(repertoryOut)) {
            throw new BizException(ErrorCode.REPERTORY_CHANGE_PARAMS_ERROR);
        }
        setRepertoryOutTotalAmount(repertoryOut, repertoryOut.getOutDetailList()); //保存的之前先对订单的总入库数和总金额计算
        if (repertoryOut.getId() == null) {
            //新建
            repertoryOut.setRefType(RepertoryRefType.DAMAGE_OUT.name()); //如果没有设置，也就是不是采购入库，直接设置为直调入库
            repertoryOut.setStatus(RepertoryOutStatus.INIT.name());
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
        }else {//修改
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
            oldOrder.setComment(repertoryOut.getComment());
            oldOrder.setOutDate(repertoryOut.getOutDate());
            oldOrder.setStatus("INIT");
            oldOrder.setReviewOrderUser("");
            oldOrder.setReviewOrderResult("");
            oldOrder.setRefOrderNumber(repertoryOut.getRefOrderNumber());
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
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (RepertoryOutDetail detail : details) {
            totalQuantity = totalQuantity.add(detail.getQuantity()); //金额
            totalAmount=totalAmount.add(detail.getQuantity().multiply(detail.getPrice()));
        }
        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
    }



    public List<RepertoryOut> getList(ReceiveListReq listReq) {
        List<RepertoryOut> orders = repertoryOutMapper.getList(listReq);
        return orders;
    }

    public List<RepertoryOutDetail> getDetailList(Long orderId) {
        List<RepertoryOutDetail> details = repertoryOutDetailMapper.getByOrderId(orderId);
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> goodsIdList = new ArrayList<>();
        details.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
        List<Goods> goods = goodsService.getGoodsById(goodsIdList);
        Map<Long, Goods> goodsMap = new HashMap<>();
        goods.stream().forEach(item -> goodsMap.put(item.getId(), item));
        details.stream().forEach(item -> item.setGoods(goodsMap.get(item.getGoodsId())));
        return details;
    }

    public List<RepertoryOut> getOutListById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<RepertoryOut> repertoryOutList = repertoryOutMapper.getOutListById(ids);
        return repertoryOutList;
    }
    public List<RepertoryOutDetail> getOutDetailList(RepertoryOutListReq reqlist) {


        List<RepertoryOutDetail> details = repertoryOutDetailMapper.getOutDetailList(reqlist);



        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        //出库记录需要使用的库存的信息而非商品模板表中的信息
        List<Long> storeIdList = new ArrayList<>();
        details.stream().forEach(item -> storeIdList.add(item.getRepertoryInfoId()));
        Map<Long, RepertoryInfo> repertoryInfoMap =  repertoryService.getMapByIdList(storeIdList);
        details.stream().forEach(item -> item.setRepertoryInfo(repertoryInfoMap.get(item.getRepertoryInfoId())));

//        details.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
//        List<Goods> goods = goodsService.getGoodsById(goodsIdList);
//        Map<Long, Goods> goodsMap = new HashMap<>();
//        goods.stream().forEach(item -> goodsMap.put(item.getId(), item));
//        details.stream().forEach(item -> item.setGoods(goodsMap.get(item.getGoodsId())));

        List<Long> outIdList = new ArrayList<>();
        details.stream().forEach(item -> outIdList.add(item.getRepertoryOutId()));
        List<RepertoryOut> repertoryOuts = getOutListById(outIdList);
        Map<Long, RepertoryOut> repertoryOutMap = new HashMap<>();
        repertoryOuts.stream().forEach(item -> repertoryOutMap.put(item.getId(), item));
        details.stream().forEach(item -> item.setRepertoryOut(repertoryOutMap.get(item.getRepertoryOutId())));
        return details;
    }



    @Transactional
    public void setOrderOutCheck(User user, Long orderId) throws BizException {
        RepertoryOut order = repertoryOutMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            logger.warn("get order fail by id:{}", orderId);
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (RepertoryOutStatus.CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("order status is not out CHECKED status, can not out check success.");
            throw new BizException(ErrorCode.RECEIVE_ORDER_STATUS_NOT_CHECKED);
        }
        //保险检查每一个订单详情是否存在为审核通过的状态
        List<RepertoryOutDetail> details = repertoryOutDetailMapper.getByOrderId(orderId);
        if (details == null || details.isEmpty()) {
            logger.warn("order in repository check but detail list is empty. orderId:{}", orderId);
            throw new BizException(ErrorCode.RECEIVE_ORDER_DETAIL_EMPTY);
        }
        boolean checkStatus = true;
        for (RepertoryOutDetail detail : details) {
            if (!RepertoryOutStatus.REVIEW.name().equals(detail.getStatus())) {
                checkStatus = false;
                break;
            }
        }
        if (!checkStatus) {
            logger.warn("order detail have check status is false. orderId:{}", orderId);
            throw new BizException(ErrorCode.RECEIVE_ORDER_STATUS_NOT_CHECKED);
        }
        Warehouse warehouse = warehouseMapper.selectByPrimaryKey(order.getWarehouseId());
        if (warehouse == null) {
            logger.error("order get warehouse fail.");
            throw new BizException(ErrorCode.RECEIVE_ORDER_WAREHOUSE_NULL);
        }
        if (!WarehouseStatus.NORMAL.name().equalsIgnoreCase(warehouse.getStatus())) {
            logger.warn("warehouse is frozen now, status is not normal. can not do repertory in. warehouse:{} order:{}",
                    order.getWarehouseId(), order.getId());
            throw new BizException(ErrorCode.OUT_ORDER_WAREHOUSE_FROZEN);
        }
        //从库存中扣减数据
       // List<RepertoryInfo> infoList=new ArrayList<>();
        for(RepertoryOutDetail detail:details){
            Long infoid= detail.getRepertoryInfoId();
            RepertoryInfo tempR= repertoryInfoMapper.selectByPrimaryKey(infoid);
            if(tempR.getQuantity().compareTo(detail.getQuantity())>=0){
                tempR.setQuantity(tempR.getQuantity().subtract(detail.getQuantity()));
                //tempR.setUpdateBy(user.getNickname());
                //tempR.setUpdateTime(new Date());
                //infoList.add(tempR);
                repertoryInfoMapper.updateByPrimaryKeySelective(tempR);
            }else{
                throw new BizRuntimeException(ErrorCode.OUT_ORDER_QUANTITY_ERROR);
            }

        }


        //审核通过，修改出库单及其明细状态
        order.setStatus(RepertoryOutStatus.CHECKED.name());
        order.setCheckOrderUser(user.getNickname());
        order.setCheckDate(new Date());
        repertoryOutMapper.updateByPrimaryKeySelective(order);
        for (RepertoryOutDetail detail : details) {
            detail.setStatus(RepertoryOutStatus.CHECKED.name());
            detail.setCheckDate(new Date());
            detail.setCheckUser(user.getNickname());
            repertoryOutDetailMapper.updateByPrimaryKeySelective(detail);
        }


        // 生成一个入库信息
        //rabbitmqQueueConfig.sendMessage("RepertoryInService", RabbitmqQueueConfig.ORDER_BUY, order);
    }


    public void setCheckResult(User user, ReceiveSetReq setReq) throws BizException {
        if (setReq == null || (setReq.getOrderId() == null && setReq.getDetailId() == null)) {
            logger.warn("request params order id or detail id is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (setReq.getOrderId() != null) {
            //整单验证通过
            reviewOneOrder(user, setReq);
        }/*else if (setReq.getDetailId() != null) {
            //单笔详情验证
            reviewOneDetail(user, setReq);
        }*/else {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
    }



    @Transactional
    public void reviewOneOrder(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryOut order = repertoryOutMapper.selectByPrimaryKey(setReq.getOrderId());
        if (order == null) {
            logger.warn("get order info fail by id:{}", setReq.getOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (!order.getCompanyId().equals(user.getCompanyId())) {
            logger.error("user get company is not match. orderId:{} user:{}", setReq.getOrderId(), user.getId());
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }

        //获取该出库单下所有出库明细
        List<RepertoryOutDetail> rOutList=  repertoryOutDetailMapper.getByOrderId(order.getId());
        if(validateReviewOneOrder(rOutList)==false){
            throw new BizRuntimeException(ErrorCode.OUT_REVIEW_STATE_ERROR);
        }

        for(int i=0;i<rOutList.size();i++){
            RepertoryOutDetail tempOutDetail= rOutList.get(i);
            //tempOutDetail.setStatus(RepertoryOutStatus.REVIEW.name());
            //tempOutDetail.setReviewUser(user.getNickname());
            //保存复核结果
            tempOutDetail.setReviewResult(setReq.getReviewResult());
            tempOutDetail.setReviewUser(setReq.getReviewUser());
            tempOutDetail.setStatus(setReq.getStatus());
            tempOutDetail.setUpdatedBy(user.getNickname());
            tempOutDetail.setUpdatedTime(new Date());
            repertoryOutDetailMapper.updateByPrimaryKeySelective(tempOutDetail);
        }
        order.setStatus(setReq.getStatus());
        order.setReviewOrderUser(setReq.getReviewUser());
        order.setReviewOrderResult(setReq.getReviewResult());
        repertoryOutMapper.updateByPrimaryKeySelective(order);
    }

    public boolean validateReviewOneOrder(List<RepertoryOutDetail> outDetailList){
        boolean isReview=true;
        for(RepertoryOutDetail onedetail:outDetailList){
           String strStatus= onedetail.getStatus();
           //状态处于 INIT 或者为空 可以进行复核
           if(strStatus!=null  && ! strStatus.equals(RepertoryOutStatus.INIT.name())){
               isReview=false;
               break;
           }
        }
        return isReview;
    }

    public List<RepertoryOutSider> getUnchecked(int companyId, String type){
        return repertoryOutDetailMapper.getUnchecked(companyId, type);
    }

    public List<RepertoryOutList> getOutListDamage(Long id){

        return repertoryOutMapper.getOutListDamage(id);
    }

    public List<RepertoryOutList> getOutListChange(Long id){

        return repertoryOutMapper.getOutListChange(id);
    }

    public int deleteOrder(Long id){
        return repertoryOutMapper.deleteOrder(id);
    }

    /**
     * 复核一条，暂废
     * @param user
     * @param setReq
     * @throws BizException
     */
/*    @Transactional
    public void reviewOneDetail(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryOutDetail detail = repertoryOutDetailMapper.selectByPrimaryKey(setReq.getDetailId());
        if (detail == null) {
            logger.warn("get order detail fail by id:{}", setReq.getDetailId());
            throw new BizException(ErrorCode.REPERTORY_CHANGE_PARAMS_ERROR);
        }
        RepertoryOut repertoryOut = repertoryOutMapper.selectByPrimaryKey(detail.getRepertoryOutId());
        if (repertoryOut == null) {
            logger.warn("get order fail by id:{}", detail.getRepertoryOutId());
            throw new BizException(ErrorCode.REPERTORY_CHANGE_PARAMS_ERROR);
        }
        String strStatus=detail.getStatus();
        //不是初始化状态的工单无法进行复核操作
        if(strStatus!=null && !RepertoryOutStatus.INIT.name().equals(strStatus)){
            throw new BizRuntimeException(ErrorCode.OUT_REVIEW_STATE_ERROR);
        }
        detail.setStatus(setReq.getStatus());
        detail.setReviewUser(setReq.getReviewUser());
        repertoryOutDetailMapper.updateByPrimaryKeySelective(detail);

        List<RepertoryOutDetail> details = repertoryOutDetailMapper.getByOrderId(repertoryOut.getId());

        boolean isFinish=true;
        for (RepertoryOutDetail item : details) {
            if (!RepertoryOutStatus.REVIEW.name().equals(item.getStatus())) {
                isFinish =false;
                break;
            }
        }
        if (isFinish) {
            repertoryOut.setStatus(setReq.getStatus());
            repertoryOutMapper.updateByPrimaryKeySelective(repertoryOut);
        }
    }*/

    /**
     * 双人复核，暂废
     * @param user
     * @param setReq
     * @throws BizException
     */
    //双人复核整张出库单
/*    @Transactional
    public void reviewNextOneOrder(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryOut order = repertoryOutMapper.selectByPrimaryKey(setReq.getOrderId());
        if (order == null) {
            logger.warn("get RepertoryOut info fail by id:{}", setReq.getOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (!order.getCompanyId().equals(user.getCompanyId())) {
            logger.error("user get company is not match. orderId:{} user:{}", setReq.getOrderId(), user.getId());
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }

        //获取该出库单下所有出库明细
        List<RepertoryOutDetail> rOutList=  repertoryOutDetailMapper.getByOrderId(order.getId());

        boolean isReview=true;
        for(RepertoryOutDetail onedetail:rOutList){
            String strStatus= onedetail.getStatus();
            //状态处于 INIT 或者为空 可以进行复核
            if(!RepertoryOutStatus.REVIEW.name().equals(strStatus)){
                isReview=false;
                break;
            }
        }

        if(isReview==false){
            throw new BizRuntimeException(ErrorCode.OUT_REVIEW_STATE_ERROR);
        }
        //将明细都设置为双人复核状态
        for(int i=0;i<rOutList.size();i++){
            RepertoryOutDetail tempOutDetail= rOutList.get(i);
            tempOutDetail.setStatus(RepertoryOutStatus.REVIEW_NEXT.name());
            tempOutDetail.setReviewNextUserId(user.getId());
            tempOutDetail.setReviewResult(setReq.getCheckResult());
            tempOutDetail.setUpdatedBy(user.getNickname());
            tempOutDetail.setUpdatedTime(new Date());
            repertoryOutDetailMapper.updateByPrimaryKeySelective(tempOutDetail);
        }
        order.setStatus(RepertoryOutStatus.REVIEW_NEXT.name());
        repertoryOutMapper.updateByPrimaryKeySelective(order);
    }*/

/*    @Transactional
    public void reviewNextOneDetail(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryOutDetail detail = repertoryOutDetailMapper.selectByPrimaryKey(setReq.getDetailId());
        if (detail == null) {
            logger.warn("get order detail fail by id:{}", setReq.getDetailId());
            throw new BizException(ErrorCode.REPERTORY_CHANGE_PARAMS_ERROR);
        }
        RepertoryOut repertoryOut = repertoryOutMapper.selectByPrimaryKey(detail.getRepertoryOutId());
        if (repertoryOut == null) {
            logger.warn("get order fail by id:{}", detail.getRepertoryOutId());
            throw new BizException(ErrorCode.REPERTORY_CHANGE_PARAMS_ERROR);
        }
        String strStatus=detail.getStatus();
        //不是初始化状态的工单无法进行复核操作
        if(!RepertoryOutStatus.REVIEW.name().equals(strStatus)){
            throw new BizRuntimeException(ErrorCode.OUT_REVIEW_STATE_ERROR);
        }
        detail.setStatus(RepertoryOutStatus.REVIEW_NEXT.name());
        detail.setReviewUser(user.getNickname());
        repertoryOutDetailMapper.updateByPrimaryKeySelective(detail);

        List<RepertoryOutDetail> details = repertoryOutDetailMapper.getByOrderId(repertoryOut.getId());

        boolean isFinish=true;
        for (RepertoryOutDetail item : details) {
            if (!RepertoryOutStatus.REVIEW_NEXT.name().equals(item.getStatus())) {
                isFinish =false;
                break;
            }
        }
        if (isFinish) {
            repertoryOut.setStatus(RepertoryOutStatus.REVIEW_NEXT.name());
            repertoryOutMapper.updateByPrimaryKeySelective(repertoryOut);
        }
    }*/
}
