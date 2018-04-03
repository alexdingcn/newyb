package com.yiban.erp.service.buy;

import com.yiban.erp.constant.BuyOrderStatus;
import com.yiban.erp.constant.OptionsType;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.constant.RepositoryOrderStatus;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.entities.*;
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
import sun.nio.cs.US_ASCII;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReceiveService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveService.class);

    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private BuyOrderMapper buyOrderMapper;
    @Autowired
    private RepositoryOrderMapper repositoryOrderMapper;
    @Autowired
    private RepositoryOrderDetailMapper repositoryOrderDetailMapper;
    @Autowired
    private OptionsMapper optionsMapper;
    @Autowired
    private BuyOrderDetailMapper buyOrderDetailMapper;
    @Autowired
    private GoodsMapper goodsMapper;

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
    public void saveOrder(User user, RepositoryOrder order) throws BizException {
        if (!saveValidate(order)) {
            throw new BizException(ErrorCode.RECEIVE_SAVE_PRAMS_INVALID);
        }
        if (order.getId() == null) {
            //新建
            order.setCompanyId(user.getCompanyId());
            order.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.IN_CHECK));
            order.setCreateBy(user.getNickname());
            order.setCreateTime(new Date());
            int count = repositoryOrderMapper.insert(order);
            if (count <= 0 || order.getId() == null) {
                logger.warn("save order insert fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }else {
            RepositoryOrder oldOrder = repositoryOrderMapper.selectByPrimaryKey(order.getId());
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
            int count = repositoryOrderMapper.updateByPrimaryKeySelective(order);
            if (count <= 0) {
                logger.warn("save order update fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
            }
        }
        logger.info("begin save repository order details.");
        saveOrderDetail(user, order);

        //如果是保存操作，不是暂挂，验证是否存在采购单号，如果存在，修改到已经收货的状态
        if (RepositoryOrderStatus.INIT.name().equalsIgnoreCase(order.getStatus()) && order.getBuyOrderId() != null) {
            logger.info("set buy order status to shiped. buyOrderId:{}", order.getBuyOrderId());
            BuyOrder buyOrder = buyOrderMapper.selectByPrimaryKey(order.getBuyOrderId());
            if (buyOrder != null) {
                buyOrder.setStatus(BuyOrderStatus.SHIPPED.name());
                buyOrder.setUpdatedBy(user.getNickname());
                buyOrder.setUpdatedTime(new Date());
                buyOrderMapper.updateByPrimaryKeySelective(buyOrder);
            }
        }
    }

    private int saveOrderDetail(User user, RepositoryOrder order) {
        List<RepositoryOrderDetail> details = order.getDetails();
        if (!order.canUpdateDetail()) {
            logger.error("can update detail result is false. user:{} orderId:{}", user.getId(), order.getId());
            return -1;
        }
        //直接删除原有的，然后重新插入数据
        repositoryOrderDetailMapper.deleteByOrderId(order.getId());
        details.stream().forEach(item -> {
            item.setRepositoryOrderId(order.getId());
            item.setCreateBy(user.getNickname());
            item.setCreateTime(new Date());
        });
        return repositoryOrderDetailMapper.insertBatch(details);
    }

    private boolean saveValidate(RepositoryOrder order) {
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

    public List<RepositoryOrder> getList(ReceiveListReq listReq) {
        List<RepositoryOrder> orders = repositoryOrderMapper.getList(listReq);
        if (orders == null || orders.isEmpty()) {
            return orders;
        }
        List<Long> orderIdList = new ArrayList<>();
        //获取option进行设置
        List<String> queryOptions = Arrays.asList(
                OptionsType.TEMPER_CONTROL.name(),
                OptionsType.TEMPER_STATUS.name(),
                OptionsType.SHIP_TOOL.name(),
                OptionsType.PAY_METHOD.name(),
                OptionsType.SHIP_METHOD.name(),
                OptionsType.BUY_TYPE.name(),
                OptionsType.BILL_TYPE.name()
        );
        List<Options> options = optionsMapper.findByTypes(listReq.getCompanyId(), queryOptions);
        orders.stream().forEach(item -> {item.setOptions(options); orderIdList.add(item.getId());});
        //获取所有订单对应的详情信息
        List<RepositoryOrderDetail> details = repositoryOrderDetailMapper.getByOrderIdList(orderIdList);
        if (details ==null || details.isEmpty()) {
            return orders;
        }
        Map<Long, List<RepositoryOrderDetail>> detailMap = details.stream().collect(Collectors.groupingBy(RepositoryOrderDetail::getRepositoryOrderId));
        orders.stream().forEach(item -> item.setDetails(detailMap.get(item.getId())));
        return orders;
    }

    public List<RepositoryOrderDetail> getDetailList(Long orderId) {
        return repositoryOrderDetailMapper.getByOrderIdList(Arrays.asList(orderId));
    }

    public void removeById(User user, Long id) throws BizException{
        if (id == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        RepositoryOrder order = repositoryOrderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (!RepositoryOrderStatus.TEMP_STORAGE.name().equalsIgnoreCase(order.getStatus())) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_CAN_NOT_REMOVE);
        }
        if (!user.getCompanyId().equals(order.getCompanyId())) {
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        order.setStatus(RepositoryOrderStatus.DELETE.name());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        repositoryOrderMapper.updateByPrimaryKeySelective(order);
    }

    public RepositoryOrder getByBuyOrder(User user, Long buyOrderId) throws BizException {
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
        RepositoryOrder order = repositoryOrderMapper.getByBuyOrder(user.getCompanyId(), buyOrderId);
        if (order == null) {
            logger.info("buy order is not exist in receive order. buy order id:{}", buyOrderId);
            return makeReceiveOrderByBuyOrder(user, buyOrder);
        }else {
            //获取option进行设置
            List<String> queryOptions = Arrays.asList(
                    OptionsType.TEMPER_CONTROL.name(),
                    OptionsType.TEMPER_STATUS.name(),
                    OptionsType.SHIP_TOOL.name(),
                    OptionsType.PAY_METHOD.name(),
                    OptionsType.SHIP_METHOD.name(),
                    OptionsType.BUY_TYPE.name(),
                    OptionsType.BILL_TYPE.name()
            );
            List<Options> options = optionsMapper.findByTypes(user.getCompanyId(), queryOptions);
            order.setOptions(options);
            //获取所有订单对应的详情信息
            List<RepositoryOrderDetail> details = repositoryOrderDetailMapper.getByOrderIdList(Arrays.asList(order.getId()));
            if (details ==null || details.isEmpty()) {
                return order;
            }
            Map<Long, List<RepositoryOrderDetail>> detailMap = details.stream().collect(Collectors.groupingBy(RepositoryOrderDetail::getRepositoryOrderId));
            order.setDetails(detailMap.get(order.getId()));
            return order;
        }
    }

    private RepositoryOrder makeReceiveOrderByBuyOrder(User user, BuyOrder buyOrder) {
        RepositoryOrder order = new RepositoryOrder();
        order.setCompanyId(buyOrder.getCompanyId());
        order.setBuyOrderId(buyOrder.getId());
        order.setSupplierId(buyOrder.getSupplierId());
        order.setSupplierName(buyOrder.getSupplier());
        order.setSupplierContactId(buyOrder.getSupplierContactId());
        order.setSupplierContactName(buyOrder.getSupplierContact());
        order.setBuyerId(buyOrder.getBuyerId());
        order.setTempControlMethod(buyOrder.getTemperControlId() == null ? null : Long.valueOf(buyOrder.getTemperControlId()));
        order.setShipMethod(buyOrder.getShipMethodId() == null ? null : Long.valueOf(buyOrder.getShipMethodId()));
        order.setShipTool(buyOrder.getShipToolId() == null ? null : Long.valueOf(buyOrder.getShipToolId()));
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
        List<Goods> goodsList = goodsMapper.selectByIdList(goodsIdList);
        Map<Long, Goods> goodsMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodsMap.put(item.getId(), item));

        List<RepositoryOrderDetail> details = new ArrayList<>();

        buyOrderDetails.stream().forEach(item -> {
            RepositoryOrderDetail detail = new RepositoryOrderDetail();
            detail.setGoodsId(item.getGoodsId());
            detail.setGoods(goodsMap.get(item.getGoodsId()));
            detail.setReceiveQuality(item.getQuantity() == null ? 0 : item.getQuantity().intValue());
            detail.setBigQuality(0);
            detail.setPrice(item.getBuyPrice());
            detail.setAmount(item.getAmount());
            details.add(detail);
        });

        order.setDetails(details);
        return order;
    }

    public void setOrderCheckTemp(ReceiveSetReq setReq, User user) throws BizException {
        RepositoryOrder order = repositoryOrderMapper.selectByPrimaryKey(setReq.getOrderId());
        if (order == null ) {
            logger.warn("get order fail by id:{}", setReq.getOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        order.setUpdateTime(new Date());
        order.setUpdateBy(user.getNickname());
        order.setCheckTemp(setReq.getCheckTemp());
        repositoryOrderMapper.updateByPrimaryKeySelective(order);
    }

    public void setDetailSurvey(User user, ReceiveSetReq setReq) throws BizException {
        RepositoryOrderDetail detail = repositoryOrderDetailMapper.selectByPrimaryKey(setReq.getDetailId());
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
        repositoryOrderDetailMapper.updateByPrimaryKeySelective(detail);
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
        RepositoryOrder order = repositoryOrderMapper.selectByPrimaryKey(setReq.getOrderId());
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
        repositoryOrderDetailMapper.setCheckByOrder(setReq);

        //直接把订单的状态修改为意见验收通过的的状态
        order.setStatus(RepositoryOrderStatus.CHECKED.name());
        order.setUpdateBy(user.getNickname());
        order.setUpdateTime(new Date());
        repositoryOrderMapper.updateByPrimaryKeySelective(order);
    }

    private void checkOneDetail(User user, ReceiveSetReq setReq) throws BizException {
        RepositoryOrderDetail detail = repositoryOrderDetailMapper.selectByPrimaryKey(setReq.getDetailId());
        if (detail == null) {
            logger.warn("get order detail fail by id:{}", setReq.getDetailId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        //直接调用验收逻辑
        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        int count = repositoryOrderDetailMapper.setCheckByDetail(setReq);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }

        //验证订单的是否意见全部验证通过，如果是，需要把订单修改为已经验收完毕的状态
        List<RepositoryOrderDetail> details = repositoryOrderDetailMapper.getByOrderIdList(Arrays.asList(detail.getRepositoryOrderId()));
        boolean checked = true;
        for (RepositoryOrderDetail item : details) {
            if (item.getCheckStatus() != null && !item.getCheckStatus()) {
                checked = false;
                break;
            }
        }
        if (checked) {
            logger.warn("user:{} check order detail:{} then order check over. orderId:{}", user.getId(), detail.getId(), detail.getRepositoryOrderId());
            repositoryOrderMapper.setCheckStatus(detail.getRepositoryOrderId(), RepositoryOrderStatus.CHECKED.name(), user.getNickname(), new Date());
        }
    }

    public void setUncheckOrder(User user, Long orderId) throws BizException {
        RepositoryOrder order = repositoryOrderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        ReceiveSetReq setReq = new ReceiveSetReq();
        setReq.setOrderId(orderId);
        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        repositoryOrderDetailMapper.setUnCheckByOrder(setReq);

        //把订单的状态修改为INIT状态
        order.setStatus(RepositoryOrderStatus.INIT.name());
        order.setUpdateTime(new Date());
        order.setUpdateBy(user.getNickname());
        repositoryOrderMapper.updateByPrimaryKeySelective(order);
    }


    public void setUncheckDetail(User user, Long detailId) throws BizException {
        RepositoryOrderDetail detail = repositoryOrderDetailMapper.selectByPrimaryKey(detailId);
        if (detail == null) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        ReceiveSetReq setReq = new ReceiveSetReq();
        setReq.setDetailId(detailId);
        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        int count = repositoryOrderDetailMapper.setUnCheckByDetail(setReq);
        if (count <=0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        repositoryOrderMapper.setCheckStatus(detail.getRepositoryOrderId(), RepositoryOrderStatus.INIT.name(), user.getNickname(), new Date());
    }
}
