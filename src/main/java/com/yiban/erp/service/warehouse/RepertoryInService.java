package com.yiban.erp.service.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.*;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.GoodsService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepertoryInService {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryInService.class);

    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private BuyOrderMapper buyOrderMapper;
    @Autowired
    private RepertoryInMapper repertoryInMapper;
    @Autowired
    private RepertoryInDetailMapper repertoryInDetailMapper;
    @Autowired
    private OptionsMapper optionsMapper;
    @Autowired
    private BuyOrderDetailMapper buyOrderDetailMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private WarehouseMapper warehouseMapper;

    /**
     * 获取某商品当前库存和申购订单信息
     * @param warehouseId
     * @param goodsIdList
     * @return
     */
    public Map<Long, CurrentBalanceResp> getCurrentBalance(Integer warehouseId, List<Long> goodsIdList) {
        if (warehouseId == null || goodsIdList == null || goodsIdList.isEmpty()) {
            return null;
        }
        //获取当前仓库内的所有数量
        List<CurrentBalanceResp> balanceResp = repertoryInfoMapper.getBalance(warehouseId, goodsIdList);
        List<CurrentBalanceResp> lastPriceResp = repertoryInfoMapper.getLastBuyPrice(warehouseId, goodsIdList);
        List<CurrentBalanceResp> orderResp = buyOrderMapper.getGoodsOrderCount(goodsIdList);

        //根据goodsId 分组
        Map<Long, List<CurrentBalanceResp>> balanceMap = balanceResp.stream().collect(Collectors.groupingBy(CurrentBalanceResp::getGoodsId));
        Map<Long, List<CurrentBalanceResp>> lastPriceMap = lastPriceResp.stream().collect(Collectors.groupingBy(CurrentBalanceResp::getGoodsId));
        Map<Long, List<CurrentBalanceResp>> orderRespMap = orderResp.stream().collect(Collectors.groupingBy(CurrentBalanceResp::getGoodsId));

        Map<Long, CurrentBalanceResp> resp = new HashMap<>();
        for (Long goodsId : goodsIdList) {
            CurrentBalanceResp mapVal = new CurrentBalanceResp();
            mapVal.setGoodsId(goodsId);
            List<CurrentBalanceResp> bs = balanceMap.get(goodsId);
            if (bs != null && !bs.isEmpty()) {
                CurrentBalanceResp bsItem = bs.get(0);
                mapVal.setBalance(bsItem != null ? bsItem.getBalance() : null);
            }
            List<CurrentBalanceResp> ls = lastPriceMap.get(goodsId);
            if (ls != null && !ls.isEmpty()) {
                CurrentBalanceResp lsItem = ls.get(0);
                mapVal.setLastPrice(lsItem != null ? lsItem.getLastPrice() : null);
            }
            List<CurrentBalanceResp> os = orderRespMap.get(goodsId);
            if (os != null && !os.isEmpty()) {
                CurrentBalanceResp osItem = os.get(0);
                mapVal.setBuyOrderCount(osItem != null ? osItem.getBuyOrderCount() : null);
                mapVal.setOngoingCount(osItem != null ? osItem.getOngoingCount() : null);
            }
            resp.put(goodsId, mapVal);
        }

        return resp;
    }

    /**
     * 保存收货入库订单信息
     * @param user
     * @param order
     * @throws BizException
     */
    public void saveOrder(User user, RepertoryIn order) throws BizException {
        if (!saveValidate(order)) {
            throw new BizException(ErrorCode.RECEIVE_SAVE_PRAMS_INVALID);
        }
        if (order.getId() == null) {
            //新建
            order.setCompanyId(user.getCompanyId());
            order.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.IN_CHECK));
            order.setCreateBy(user.getNickname());
            order.setCreateTime(new Date());
            int count = repertoryInMapper.insert(order);
            if (count <= 0 || order.getId() == null) {
                logger.warn("save order insert fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }else {
            RepertoryIn oldOrder = repertoryInMapper.selectByPrimaryKey(order.getId());
            if (oldOrder == null) {
                logger.warn("get repository order fail by id:{}", order.getId());
                throw new BizException(ErrorCode.RECEIVE_ORDER_NOT_FOUND);
            }
            if (oldOrder.getCompanyId() == null || !oldOrder.getCompanyId().equals(user.getCompanyId())) {
                logger.warn("old order company is not match user company. order id:{}, user id:{}", order.getId(), user.getId());
                throw new BizException(ErrorCode.ACCESS_PERMISSION);
            }
            if (!oldOrder.canUpdateDetail()) {
                logger.warn("order can not update. order id:{}", oldOrder.getId());
                throw new BizException(ErrorCode.RECEIVE_ORDER_CANNOT_UPDATE);
            }
            order.setUpdateBy(user.getNickname());
            order.setUpdateTime(new Date());
            int count = repertoryInMapper.updateByPrimaryKeySelective(order);
            if (count <= 0) {
                logger.warn("save order update fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }
        logger.info("begin save repository order details.");
        saveOrderDetail(user, order);

        //如果是保存操作，验证是否存在采购单号，如果存在，修改到已经收货的状态
        if (RepertoryRefType.BUY_ORDER.name().equalsIgnoreCase(order.getRefType())
                && order.getRefOrderId() != null) {
            logger.info("set buy order status to shiped. buyOrderId:{}", order.getRefOrderId());
            BuyOrder buyOrder = buyOrderMapper.selectByPrimaryKey(order.getRefOrderId());
            if (buyOrder != null) {
                buyOrder.setStatus(BuyOrderStatus.SHIPPED.name());
                buyOrder.setUpdatedBy(user.getNickname());
                buyOrder.setUpdatedTime(new Date());
                buyOrderMapper.updateByPrimaryKeySelective(buyOrder);
            }
        }
    }

    private int saveOrderDetail(User user, RepertoryIn order) {
        List<RepertoryInDetail> details = order.getDetails();
        if (!order.canUpdateDetail()) {
            logger.error("can update detail result is false. user:{} orderId:{}", user.getId(), order.getId());
            return -1;
        }
        //直接删除原有的，然后重新插入数据
        repertoryInDetailMapper.deleteByOrderId(order.getId());
        details.stream().forEach(item -> {
            item.setInOrderId(order.getId());
            item.setCreateBy(user.getNickname());
            item.setCreateTime(new Date());
        });
        return repertoryInDetailMapper.insertBatch(details);
    }

    private boolean saveValidate(RepertoryIn order) {
        if (order == null) {
            logger.warn("save order is null");
            return false;
        }
        if (order.getSupplierId() == null) {
            logger.warn("save order but supplier id is null.");
            return false;
        }
        if (order.getSupplierContactId() == null) {
            logger.warn("save order but supplier contact id is null.");
            return false;
        }
        if (order.getWarehouseId() == null) {
            logger.warn("save order but warehouse id is null.");
            return false;
        }
        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            logger.warn("save order but details list is empty.");
            return false;
        }
        return true;
    }

    public List<RepertoryIn> getList(ReceiveListReq listReq) {
        List<RepertoryIn> orders = repertoryInMapper.getList(listReq);
        if (orders == null || orders.isEmpty()) {
            return orders;
        }
        setOptionsName(orders);
        return orders;
    }

    private void setOptionsName(List<RepertoryIn> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        Set<Long> optionIdSet = new HashSet<>();
        orders.stream().forEach(item -> optionIdSet.addAll(item.getOptionIdList()));
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        orders.stream().forEach(item -> item.setOptionName(options));
    }

    private void setOptionsName(RepertoryIn order) {
        if (order == null || order.getOptionIdList() == null || order.getOptionIdList().isEmpty()) {
            return;
        }
        Set<Long> optionIdSet = order.getOptionIdList();
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        order.setOptionName(options);
    }

    public List<RepertoryInDetail> getDetailList(Long orderId) {
        List<RepertoryInDetail> details = repertoryInDetailMapper.getByOrderId(orderId);
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

    public void removeById(User user, Long id) throws BizException{
        if (id == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (!RepertoryInStatus.TEMP_STORAGE.name().equalsIgnoreCase(order.getStatus())) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_CAN_NOT_REMOVE);
        }
        if (!user.getCompanyId().equals(order.getCompanyId())) {
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        order.setStatus(RepertoryInStatus.DELETE.name());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        repertoryInMapper.updateByPrimaryKeySelective(order);
    }

    public RepertoryIn getByBuyOrder(User user, Long buyOrderId) throws BizException {
        logger.debug("user:{} get receive order by buy order id:{}", user.getId(), buyOrderId);
        BuyOrder buyOrder = buyOrderMapper.getOrderById(buyOrderId);
        if (buyOrder == null) {
            logger.warn("get buy order fail. id:{}", buyOrderId);
            throw new BizException(ErrorCode.BUY_ORDER_NOT_EXISTED);
        }
        if (!user.getCompanyId().equals(buyOrder.getCompanyId())) {
            logger.warn("user company not equals buy order company: user:{} orderId:{}", user.getId(), buyOrderId);
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        if (!BuyOrderStatus.CHECKED.name().equalsIgnoreCase(buyOrder.getStatus())) {
            logger.warn("get buy order but status is error. order id:{}", buyOrderId);
            throw new BizException(ErrorCode.RECEIVE_BUY_ORDER_STATUS);
        }
        //查询当前采购收货单中是否存在该笔订单数据，如果存在，直接返回，如果不存在，生成对应数据
        RepertoryIn order = repertoryInMapper.getByRefOrder(user.getCompanyId(), RepertoryRefType.BUY_ORDER.name(), buyOrderId);
        if (order == null) {
            logger.info("buy order is not exist in receive order. buy order id:{}", buyOrderId);
            return makeReceiveOrderByBuyOrder(user, buyOrder);
        }else {
            //获取所有订单对应的详情信息
            List<RepertoryInDetail> details = getDetailList(order.getId());
            order.setDetails(details);
            return order;
        }
    }

    private RepertoryIn makeReceiveOrderByBuyOrder(User user, BuyOrder buyOrder) {
        RepertoryIn order = new RepertoryIn();
        order.setCompanyId(buyOrder.getCompanyId());
        order.setRefOrderId(buyOrder.getId());
        order.setRefType(RepertoryRefType.BUY_ORDER.name());
        order.setSupplierId(buyOrder.getSupplierId());
        order.setSupplierName(buyOrder.getSupplier());
        order.setSupplierContactId(buyOrder.getSupplierContactId());
        order.setSupplierContactName(buyOrder.getSupplierContact());
        order.setBuyerId(buyOrder.getBuyerId());
        order.setTempControlMethod(buyOrder.getTemperControlId() == null ? null : buyOrder.getTemperControlId());
        order.setShipMethod(buyOrder.getShipMethodId() == null ? null : buyOrder.getShipMethodId());
        order.setShipTool(buyOrder.getShipToolId() == null ? null : buyOrder.getShipToolId());
        order.setShipEndDate(buyOrder.getEta());
        order.setWarehouseId(buyOrder.getWarehouseId());
        order.setWarehouseName(buyOrder.getWarehouse());

        //获取采购单的详情信息
        List<BuyOrderDetail> buyOrderDetails = buyOrderDetailMapper.findByOrderId(buyOrder.getId(), user.getCompanyId());
        if (buyOrderDetails == null || buyOrderDetails.isEmpty()) {
            return order;
        }
        List<Long> goodsIdList = new ArrayList<>();
        buyOrderDetails.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
        List<Goods> goodsList = goodsService.getGoodsById(goodsIdList);
        Map<Long, Goods> goodsMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodsMap.put(item.getId(), item));

        List<RepertoryInDetail> details = new ArrayList<>();

        buyOrderDetails.stream().forEach(item -> {
            Goods goods = goodsMap.get(item.getGoodsId());
            RepertoryInDetail detail = new RepertoryInDetail();
            detail.setGoodsId(item.getGoodsId());
            detail.setGoods(goods);
            detail.setReceiveQuality(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity());
            detail.setBigQuality(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity());
            detail.setPrice(item.getBuyPrice());
            detail.setAmount(item.getAmount());
            detail.setTaxRate(goods != null ? goods.getInTax() : BigDecimal.ZERO);
            detail.setBuyOrderQuality(item.getQuantity());
            details.add(detail);
        });

        order.setDetails(details);
        return order;
    }

    public void setOrderCheckTemp(ReceiveSetReq setReq, User user) throws BizException {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(setReq.getOrderId());
        if (order == null ) {
            logger.warn("get order fail by id:{}", setReq.getOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        order.setUpdateTime(new Date());
        order.setUpdateBy(user.getNickname());
        order.setCheckTemp(setReq.getCheckTemp());
        repertoryInMapper.updateByPrimaryKeySelective(order);
    }

    public void setDetailSurvey(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryInDetail detail = repertoryInDetailMapper.selectByPrimaryKey(setReq.getDetailId());
        if (detail == null) {
            logger.warn("get detail record fail by id:{}", setReq.getDetailId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (setReq.getSurveyQuality() == null || setReq.getSurveyDate() == null || StringUtils.isBlank(setReq.getSurveyUser())) {
            logger.warn("request params is error.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        detail.setSurveyDate(setReq.getSurveyDate());
        detail.setSurveyQuality(setReq.getSurveyQuality());
        detail.setSurveyUser(setReq.getSurveyUser());
        detail.setSurveyAddress(setReq.getSurveyAddress());
        detail.setSurveyResult(setReq.getSurveyResult());
        detail.setSurveyTarget(setReq.getSurveyTarget());
        detail.setUpdateBy(user.getNickname());
        detail.setUpdateTime(new Date());
        repertoryInDetailMapper.updateByPrimaryKeySelective(detail);
    }

    public void setCheckResult(User user, ReceiveSetReq setReq) throws BizException {
        if (setReq == null || (setReq.getOrderId() == null && setReq.getDetailId() == null)) {
            logger.warn("request params order id or detail id is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (setReq.getOrderId() != null) {
            //整单验证通过
            checkOneOrder(user, setReq);
        }else if (setReq.getDetailId() != null) {
            //单笔详情验证
            checkOneDetail(user, setReq);
        }else {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
    }

    private void checkOneOrder(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(setReq.getOrderId());
        if (order == null) {
            logger.warn("get order info fail by id:{}", setReq.getOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (!order.getCompanyId().equals(user.getCompanyId())) {
            logger.error("user get company is not match. orderId:{} user:{}", setReq.getOrderId(), user.getId());
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        repertoryInDetailMapper.setCheckByOrder(setReq);

        //直接把订单的状态修改为意见验收通过的的状态
        order.setStatus(RepertoryInStatus.CHECKED.name());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        repertoryInMapper.updateByPrimaryKeySelective(order);
    }

    private void checkOneDetail(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryInDetail detail = repertoryInDetailMapper.selectByPrimaryKey(setReq.getDetailId());
        if (detail == null) {
            logger.warn("get order detail fail by id:{}", setReq.getDetailId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        //直接调用验收逻辑
        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        int count = repertoryInDetailMapper.setCheckByDetail(setReq);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }

        //验证订单的是否意见全部验证通过，如果是，需要把订单修改为已经验收完毕的状态
        List<RepertoryInDetail> details = repertoryInDetailMapper.getByOrderId(detail.getInOrderId());
        boolean checked = true;
        for (RepertoryInDetail item : details) {
            if (item.getCheckStatus() != null && !item.getCheckStatus()) {
                checked = false;
                break;
            }
        }
        if (checked) {
            logger.warn("user:{} check order detail:{} then order check over. orderId:{}", user.getId(), detail.getId(), detail.getInOrderId());
            repertoryInMapper.setCheckStatus(detail.getInOrderId(), RepertoryInStatus.CHECKED.name(), user.getNickname(), new Date());
        }
    }

    public void setUncheckOrder(User user, Long orderId) throws BizException {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        ReceiveSetReq setReq = new ReceiveSetReq();
        setReq.setOrderId(orderId);
        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        repertoryInDetailMapper.setUnCheckByOrder(setReq);

        //把订单的状态修改为INIT状态
        order.setStatus(RepertoryInStatus.INIT.name());
        order.setUpdateTime(new Date());
        order.setUpdateBy(user.getNickname());
        repertoryInMapper.updateByPrimaryKeySelective(order);
    }


    public void setUncheckDetail(User user, Long detailId) throws BizException {
        RepertoryInDetail detail = repertoryInDetailMapper.selectByPrimaryKey(detailId);
        if (detail == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        ReceiveSetReq setReq = new ReceiveSetReq();
        setReq.setDetailId(detailId);
        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        int count = repertoryInDetailMapper.setUnCheckByDetail(setReq);
        if (count <=0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        repertoryInMapper.setCheckStatus(detail.getInOrderId(), RepertoryInStatus.INIT.name(), user.getNickname(), new Date());
    }

    public void removeDetail(User user, Long detailId) throws BizException {
        RepertoryInDetail detail = repertoryInDetailMapper.selectByPrimaryKey(detailId);
        if (detail == null) {
            logger.warn("get order detail fail by id:{}", detailId);
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (detail.getCheckStatus() != null && detail.getCheckStatus()) {
            logger.warn("order detail is checked cannot remove.");
            throw new BizException(ErrorCode.RECEIVE_DETAIL_REMOVE_STATUS);
        }
        logger.info("user:{} remove receive detail info:{}", user.getId(), JSON.toJSONString(detail));
        repertoryInDetailMapper.deleteByPrimaryKey(detailId);
    }

    public int setSaveDetail(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(setReq.getOrderId());
        if (order == null) {
            logger.warn("get order by id fail. id:{}", setReq.getOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        List<RepertoryInDetail> updateList = setReq.getDetailList();
        if (updateList == null || updateList.isEmpty()) {
            logger.warn("detail update list is null.");
            return 0;
        }
        Map<Long, RepertoryInDetail> updateMap = new HashMap<>();
        updateList.stream().forEach(item -> updateMap.put(item.getId(), item));
        for (RepertoryInDetail detail : updateList) {
            if (!order.getId().equals(detail.getInOrderId())) {
                logger.warn("order id is not equals detail repository id. detailId:{} orderId:{}", detail.getId(), order.getId());
                throw new BizException(ErrorCode.RECEIVE_DETAIL_SAVE_PARAMS_ERROR);
            }
        }
        List<RepertoryInDetail> details = repertoryInDetailMapper.getByOrderId(order.getId());
        //只修改没有验证通过，如果验证都过了，不能修改
        List<RepertoryInDetail> canUpdateList = new ArrayList<>();
        details.stream().forEach(item -> {
            if (item.getCheckStatus() == null || !item.getCheckStatus()) {
                canUpdateList.add(item);
            }
        });
        if (canUpdateList.isEmpty()) {
            logger.info("order detail is all checked, can not update.");
            return 0;
        }
        int count = 0;
        for (RepertoryInDetail detail : canUpdateList) {
            RepertoryInDetail mergeItem = updateMap.get(detail.getId());
            if (mergeItem == null) {
                continue;
            }
            detail.setBatchCode(mergeItem.getBatchCode());
            detail.setProductDate(mergeItem.getProductDate());
            detail.setExpDate(mergeItem.getExpDate());
            detail.setReceiveQuality(mergeItem.getReceiveQuality());
            detail.setInCount(mergeItem.getInCount());
            detail.setRightCount(mergeItem.getRightCount());
            detail.setErrorCount(mergeItem.getErrorCount());
            detail.setAmount(mergeItem.getAmount());
            detail.setWarehouseLocation(mergeItem.getWarehouseLocation());
            int result = repertoryInDetailMapper.updateByPrimaryKeySelective(detail);
            count += result;
        }
        return count;
    }

    public void setOrderFileNo(User user, Long orderId, String fileNo) throws BizException{
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            logger.warn("get order fail by order id:{}", orderId);
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        FileInfo fileInfo = fileInfoMapper.getByFileNo(user.getCompanyId(), fileNo);
        if (fileInfo == null) {
            logger.warn("get file info fail by fileNo:{}, companyId:{}", fileNo, user.getCompanyId());
            throw new BizException(ErrorCode.FILE_GET_INFO_FAIL);
        }
        order.setFileNo(fileInfo.getFileNo());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        repertoryInMapper.updateByPrimaryKeySelective(order);
    }

    public void setOrderInCheck(User user, Long orderId) throws BizException {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            logger.warn("get order fail by id:{}", orderId);
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (!RepertoryInStatus.CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("order status is not in CHECKED status, can not in check success.");
            throw new BizException(ErrorCode.RECEIVE_ORDER_STATUS_NOT_CHECKED);
        }
        //保险检查每一个订单详情是否存在为审核通过的状态
        List<RepertoryInDetail> details = repertoryInDetailMapper.getByOrderId(orderId);
        if (details == null || details.isEmpty()) {
            logger.warn("order in repository check but detail list is empty. orderId:{}", orderId);
            throw new BizException(ErrorCode.RECEIVE_ORDER_DETAIL_EMPTY);
        }
        boolean checkStatus = true;
        for (RepertoryInDetail detail : details) {
            if (detail.getCheckStatus() == null || !detail.getCheckStatus()) {
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
            throw new BizException(ErrorCode.RECEIVE_ORDER_WAREHOUSE_FROZEN);
        }

        //如果审核通过，修改状态
        int count = repertoryInMapper.setCheckStatus(order.getId(),
                RepertoryInStatus.IN_CHECKED.name(), user.getNickname(), new Date());
        if (count <= 0) {
            logger.error("update order status fail. order id:{}", order.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        //把数据插入到库存中
        List<RepertoryInfo> infoList = createRepertoryInfos(user, order, details);
        int inCount = repertoryInfoMapper.insertBatch(infoList);
        logger.info("insert repertory info count:{}", inCount);
    }

    private List<RepertoryInfo> createRepertoryInfos(User user, RepertoryIn order, List<RepertoryInDetail> detailList) {
        List<RepertoryInfo> infos = new ArrayList<>();
        for (RepertoryInDetail detail : detailList) {
            RepertoryInfo item = new RepertoryInfo();
            item.setCompanyId(order.getCompanyId());
            item.setWarehouseId(order.getWarehouseId());
            item.setLocation(detail.getWarehouseLocation());
            item.setInUserId(user.getId());
            item.setGoodsId(detail.getGoodsId());
            item.setBatchCode(detail.getBatchCode());
            item.setFactoryId(detail.getGoods() == null ? null : detail.getGoods().getFactoryId());
            item.setInQuantity(detail.getInCount());
            item.setQuantity(detail.getInCount());
            item.setBuyPrice(detail.getPrice());
            item.setExp(false);
            item.setSaleEnable(true);
            item.setProductDate(detail.getProductDate());
            item.setExpDate(detail.getExpDate());
            item.setInDate(new Date());
            item.setSupplierId(order.getSupplierId());
            item.setSupplierContactId(order.getSupplierContactId());
            item.setBuyerId(order.getBuyerId());
            item.setOrderId(order.getId());
            item.setOrderDetailId(detail.getId());
            item.setSaleSate(detail.getSaleState());
            item.setCreateBy(user.getNickname());
            item.setCreateTime(new Date());
            item.setUpdateBy(user.getNickname());
            item.setUpdateTime(new Date());

            infos.add(item);
        }
        return infos;
    }
}
