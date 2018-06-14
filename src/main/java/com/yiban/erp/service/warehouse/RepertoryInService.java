package com.yiban.erp.service.warehouse;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.config.RabbitmqQueueConfig;
import com.yiban.erp.constant.*;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.dto.RepertoryInListReq;
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
    private WarehouseService warehouseService;
    @Autowired
    private RabbitmqQueueConfig rabbitmqQueueConfig;
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private SystemConfigService systemConfigService;

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

    private void validateSpecialManage(RepertoryIn order, Supplier supplier) throws BizException {
        //检验特殊管理药品的收货条件
        List<Long> goodsIds = new ArrayList<>();
        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            return;
        }
        for (RepertoryInDetail detail : order.getDetails()) {
            if (detail.getGoodsId() != null) {
                goodsIds.add(detail.getGoodsId());
            }
        }

        //1. 检查商品中商品中是否存在有特殊管理药品
        if (!goodsService.haveSpecialManageGoods(goodsIds)) {
            return; //没有特殊经营管理的商品，直接返回
        }
        //2. 如果存在有特殊经营管理的商品，需要验证供应商是否有该类资质
        if (supplier.getCanSpecial() == null || !supplier.getCanSpecial()) {
            logger.warn("supplier can special validate fail. supplierId:{}", supplier.getId());
            throw new BizException(ErrorCode.RECEIVE_SUPPLIER_SPECIAL_VALIDATE_FAIL);
        }

        //3. 验证是否已经是双人验收，
        String receiveUser = order.getReceiveUser();
        if (StringUtils.isEmpty(receiveUser)) {
            logger.warn("receive user is empty.");
            throw new BizException(ErrorCode.RECEIVE_SPECIAL_RECEIVE_USER_ERROR);
        }
        String[] users = receiveUser.split(";");
        String[] users1 = receiveUser.split("；");
        if (users.length < 2 && users1.length < 2) {
            logger.warn("receive user size < 2");
            throw new BizException(ErrorCode.RECEIVE_SPECIAL_RECEIVE_USER_ERROR);
        }
    }

    private void validateColdManage(RepertoryIn order, Supplier supplier) throws BizException {
        //商品冷链经营管理的验证条件
        List<Long> goodsIds = new ArrayList<>();
        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            return;
        }
        for (RepertoryInDetail detail : order.getDetails()) {
            if (detail.getGoodsId() != null) {
                goodsIds.add(detail.getGoodsId());
            }
        }
        if (!goodsService.haveColdManageGoods(goodsIds)) {
            return;
        }

        //2. 如果是冷链经营的，供应商也需要对应的资质
        if (supplier.getColdBusiness() == null || !supplier.getColdBusiness()) {
            logger.warn("supplier cold manage validate fail. supplierId:{}", supplier.getId());
            throw new BizException(ErrorCode.RECEIVE_SUPPLIER_COLD_VALIDATE_FAIL);
        }

        // 3. 如果是冷链经营信息，温控方式，到货温度，温控状态，运输方式为必输项
        if (order.getTempControlMethod() == null || order.getReceiveTemp() == null
                || order.getTempControlStatus() == null || order.getShipMethod() == null) {
            logger.warn("cold manage need params validate fail.");
            throw new BizException(ErrorCode.RECEIVE_COLD_NEED_PARAMS_ERROR);
        }
    }

    private RepertoryInStatus getSaveStatusByConfig(Integer companyId, RepertoryIn order) throws BizException {
        //先获取提交上来的状态,如果是暂存，直接返回暂存状态
        RepertoryInStatus status = RepertoryInStatus.getByName(order.getStatus());
        if (status == null) {
            logger.warn("get status fail.");
            throw new BizException(ErrorCode.RECEIVE_SAVE_STATUS_ERROR);
        }
        if (RepertoryInStatus.TEMP_STORAGE.equals(status)) {
            return RepertoryInStatus.TEMP_STORAGE;
        }
        if (!RepertoryInStatus.INIT.equals(status)) {
            //这个方法只针对收货单录入的保存
            logger.warn("repertory in request save status error.");
            throw new BizException(ErrorCode.RECEIVE_SAVE_STATUS_ERROR);
        }

        // 获取系统配置的入库流程
        Map<String, SystemConfig> configMap = systemConfigService.getConfigMap(companyId);
        boolean haveQAFlow = false;
        //获取入库质量验收流程和入库终审流程配置
        SystemConfig qaConfig = configMap.get(ConfigKey.BUY_QUALITY_CHECK.name());
        if (qaConfig != null && "open".equalsIgnoreCase(qaConfig.getKeyValue())) {
            logger.info("have quality flow.");
            haveQAFlow = true;
        }
        boolean haveFNFlow = false; //是否有终审
        SystemConfig fnConfig = configMap.get(ConfigKey.BUY_FINAL_CHECK.name());
        if (fnConfig != null && "open".equalsIgnoreCase(fnConfig.getKeyValue())) {
            haveFNFlow = true;
        }
        if (haveQAFlow) {
            return RepertoryInStatus.INIT; //设置为INIT状态，使下一步直接可以进行质量验证
        }else if (haveFNFlow) {
            return RepertoryInStatus.CHECKED; // 设置为CHECKED 状态，直接进入终审流程
        }else {
            return RepertoryInStatus.IN_CHECKED; //直接进入终审通过的状态, 这种状态是需要修改库存信息和生成财务流水的
        }
    }

    /**
     * 保存收货入库订单信息
     * @param user
     * @param order
     * @throws BizException
     */
    @Transactional
    public void saveOrder(User user, RepertoryIn order) throws BizException {
        if (!saveValidate(order)) {
            throw new BizException(ErrorCode.RECEIVE_SAVE_PRAMS_INVALID);
        }
        Supplier supplier = supplierMapper.selectByPrimaryKey(order.getSupplierId());
        if (supplier == null) {
            logger.warn("get supplier fail by id:{}", order.getSupplierId());
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //如果是暂存状态的，不需要验证下面这两个条件
        if (!RepertoryInStatus.TEMP_STORAGE.name().equalsIgnoreCase(order.getStatus())) {
            //验证特殊管制商品的条件
            validateSpecialManage(order, supplier);
            //验证冷链经营商品的条件
            validateColdManage(order, supplier);

            //根据系统配置，获取流程状态信息
            RepertoryInStatus status = getSaveStatusByConfig(user.getCompanyId(), order);
            logger.info("save next flow status is {}", status.name());
            order.setStatus(status.name());
            //如果是直接跳过终审的状态，需要验证当前仓库是否正在冻结盘库，
            if (!warehouseService.isFrozen(order.getWarehouseId())) {
                logger.warn("warehouse is frozen now, status is not normal. can not do repertory in. warehouse:{} order:{}",
                        order.getWarehouseId(), order.getId());
                throw new BizException(ErrorCode.RECEIVE_ORDER_WAREHOUSE_FROZEN);
            }
        }

        setRepertoryInTotalAmount(order, order.getDetails()); //保存的之前先对订单的总入库数和总金额计算
        if (order.getId() == null) {
            //新建
            if (order.getRefType() == null) {
                order.setRefType(RepertoryRefType.BUY_DIRECT.name()); //如果没有设置，也就是不是采购入库，直接设置为直调入库
            }
            order.setCompanyId(user.getCompanyId());
            order.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.IN_CHECK));
            order.setCreateBy(user.getNickname());
            order.setCreateTime(new Date());
            order.setUpdateBy(user.getNickname());
            order.setUpdateTime(new Date());
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
        //直接删除原有的，然后重新插入数据
        List<RepertoryInDetail> details = order.getDetails();
        repertoryInDetailMapper.deleteByOrderId(order.getId());
        details.stream().forEach(item -> {
            item.setInOrderId(order.getId());
            item.setCreateBy(user.getNickname());
            item.setCreateTime(new Date());
            item.setUpdateBy(user.getNickname());
            item.setUpdateTime(new Date());
        });
        repertoryInDetailMapper.insertBatch(details);

        //如果不是暂存状态的操作，需要处理后续逻辑
        if (!RepertoryInStatus.TEMP_STORAGE.name().equalsIgnoreCase(order.getStatus())) {
            doSaveAfter(user, order);
        }
    }

    private void doSaveAfter(User user, RepertoryIn order) {
        //验证是否存在采购单号，如果存在，修改到已经收货的状态
        if (RepertoryRefType.BUY_ORDER.name().equalsIgnoreCase(order.getRefType())
                && order.getRefOrderId() != null) {
            logger.info("set buy order status to shiped. buyOrderId:{}", order.getRefOrderId());
            BuyOrder buyOrder = buyOrderMapper.selectByPrimaryKey(order.getRefOrderId());
            if (buyOrder != null) {
                buyOrder.setStatus(BuyOrderStatus.SHIPPED);
                buyOrder.setUpdatedBy(user.getNickname());
                buyOrder.setUpdatedTime(new Date());
                buyOrderMapper.updateByPrimaryKeySelective(buyOrder);
            }
        }

        //如果状态是终审通过的状态，会直接修改库存和触发对应事件，计算财务流水
        if (RepertoryInStatus.IN_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.info("repertory in order save status is IN_CHECKED, need update repertory info and make in event. orderId:{}", order.getId());
            changeRepertoryInfo(user, order);
        }
    }

    /**
     * 注意order 和 details 的匹配
     * @param order
     * @param details
     */
    private void setRepertoryInTotalAmount(RepertoryIn order, List<RepertoryInDetail> details) {
        if (order == null || details == null || details.isEmpty()) {
            return;
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (RepertoryInDetail detail : details) {
            totalQuantity = totalQuantity.add(detail.getInCount()); //入库数量
            totalAmount = totalAmount.add(detail.getAmount()); //金额
        }
        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
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

    public List<RepertoryInDetail> getDetailList(RepertoryIn order) {
        List<RepertoryInDetail> details = repertoryInDetailMapper.getByOrderId(order.getId());
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> goodsIdList = new ArrayList<>();
        details.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
        List<Goods> goods = goodsService.getGoodsById(goodsIdList);
        //嵌入当前库存信息，最近采购价
        Integer warehouseId = order.getWarehouseId(); //逻辑上同一个订单的仓库ID相同
        List<String> options = Arrays.asList(GoodsQuery.OPTION_LW, GoodsQuery.OPTION_LB, GoodsQuery.OPTION_CBQ);
        goodsService.setGoodsExtra(null, warehouseId, options, goods);

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
        if (!user.getCompanyId().equals(order.getCompanyId())) {
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        if (!RepertoryInStatus.TEMP_STORAGE.name().equalsIgnoreCase(order.getStatus())) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_CAN_NOT_REMOVE);
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
            List<RepertoryInDetail> details = getDetailList(order);
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
        order.setSupplierColdManage(buyOrder.getSupplierColdManage());
        order.setSupplierSpecialManage(buyOrder.getSupplierSpecialManage());
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
        //嵌入当前库存信息，最近采购价
        Integer warehouseId = order.getWarehouseId(); //逻辑上同一个订单的仓库ID相同
        List<String> options = Arrays.asList(GoodsQuery.OPTION_LW, GoodsQuery.OPTION_LB, GoodsQuery.OPTION_CBQ);
        goodsService.setGoodsExtra(null, warehouseId, options, goodsList);
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

    public int setCheckResult(User user, ReceiveSetReq setReq) throws BizException {
        if (setReq == null || (setReq.getOrderId() == null && setReq.getDetailId() == null)) {
            logger.warn("request params order id or detail id is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }

        if (setReq.getOrderId() != null) {
            //整单验证通过
            return checkOneOrder(user, setReq);
        }else if (setReq.getDetailId() != null) {
            //单笔详情验证
            return checkOneDetail(user, setReq);
        }else {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
    }

    @Transactional
    public int checkOneOrder(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(setReq.getOrderId());
        if (order == null) {
            logger.warn("get order info fail by id:{}", setReq.getOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        if (!order.getCompanyId().equals(user.getCompanyId())) {
            logger.error("user get company is not match. orderId:{} user:{}", setReq.getOrderId(), user.getId());
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        //获取系统流程
        boolean haveFNFlow = systemConfigService.haveOrderFlow(user.getCompanyId(), ConfigKey.BUY_FINAL_CHECK);
        //如果没有终审，需要验证下当前仓库是否被冻结
        if (!haveFNFlow && warehouseService.isFrozen(order.getWarehouseId())) {
            logger.warn("warehouse is frozen now, status is not normal. warehouse:{} order:{}",
                    order.getWarehouseId(), order.getId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_WAREHOUSE_FROZEN);
        }

        setReq.setUpdateBy(user.getNickname());
        setReq.setUpdateTime(new Date());
        repertoryInDetailMapper.setCheckByOrder(setReq);
        if (haveFNFlow) {
            //存在有终审流程，直接修改到终审状态
            order.setStatus(RepertoryInStatus.CHECKED.name());
            order.setUpdateBy(user.getNickname());
            order.setUpdateTime(new Date());
            repertoryInMapper.updateByPrimaryKeySelective(order);
        }else {
            //已经没有终审流程，直接修改登记后直接登记到终审通过，并且触发对应事件
            order.setStatus(RepertoryInStatus.IN_CHECKED.name());
            order.setUpdateBy(user.getNickname());
            order.setUpdateTime(new Date());
            repertoryInMapper.updateByPrimaryKeySelective(order);

            changeRepertoryInfo(user, order); //修改库存
        }
        return 1;
    }

    @Transactional
    public int checkOneDetail(User user, ReceiveSetReq setReq) throws BizException {
        RepertoryInDetail detail = repertoryInDetailMapper.selectByPrimaryKey(setReq.getDetailId());
        if (detail == null) {
            logger.warn("get order detail fail by id:{}", setReq.getDetailId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        RepertoryIn repertoryIn = repertoryInMapper.selectByPrimaryKey(detail.getInOrderId());
        if (repertoryIn == null) {
            logger.warn("get order fail by id:{}", detail.getInOrderId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        //获取系统流程，
        boolean haveFNFlow = systemConfigService.haveOrderFlow(user.getCompanyId(), ConfigKey.BUY_FINAL_CHECK);
        //如果没有终审，需要验证下当前仓库是否被冻结
        if (!haveFNFlow && warehouseService.isFrozen(repertoryIn.getWarehouseId())) {
            logger.warn("warehouse is frozen now, status is not normal. warehouse:{} order:{}",
                    repertoryIn.getWarehouseId(), repertoryIn.getId());
            throw new BizException(ErrorCode.RECEIVE_ORDER_WAREHOUSE_FROZEN);
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
            if (item.getCheckStatus() == null || !item.getCheckStatus()) {
                checked = false;
                break;
            }
        }
        if (checked) {
            logger.info("user:{} check order detail:{} then order check over. orderId:{}", user.getId(), detail.getId(), detail.getInOrderId());
            if (haveFNFlow) {
                // 修改到终审状态
                repertoryIn.setStatus(RepertoryInStatus.CHECKED.name());
                repertoryIn.setUpdateTime(new Date());
                repertoryIn.setUpdateBy(user.getNickname());
                repertoryInMapper.updateByPrimaryKeySelective(repertoryIn);
            }else {
                logger.info("update to IN_CHECKED status and change repertory info.");
                // 修改到终审通过的状态
                repertoryIn.setStatus(RepertoryInStatus.IN_CHECKED.name());
                repertoryIn.setUpdateTime(new Date());
                repertoryIn.setUpdateBy(user.getNickname());
                repertoryInMapper.updateByPrimaryKeySelective(repertoryIn);

                changeRepertoryInfo(user, repertoryIn); //修改库存
            }
            return 1;  //返回1代表需要刷新整个入库单列表
        }else {
            return 0;
        }
    }

    public void setUncheckOrder(User user, Long orderId) throws BizException {
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(orderId);
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        //能取消的前提是当前这笔订单的状态不是终审通过
        if (RepertoryInStatus.IN_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("repertory in order status is IN_CHECKED, can not change uncheck");
            throw new BizException(ErrorCode.RECEIVE_QA_UNCHECK_STATUS_ERROR);
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
        RepertoryIn order = repertoryInMapper.selectByPrimaryKey(detail.getInOrderId());
        if (order == null || !user.getCompanyId().equals(order.getCompanyId())) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        //能取消的前提是当前这笔订单的状态不是终审通过
        if (RepertoryInStatus.IN_CHECKED.name().equalsIgnoreCase(order.getStatus())) {
            logger.warn("repertory in order status is IN_CHECKED, can not change uncheck");
            throw new BizException(ErrorCode.RECEIVE_QA_UNCHECK_STATUS_ERROR);
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

        // 仓库是否处于冻结状态，如果是，不能操作
        if (!warehouseService.isFrozen(order.getWarehouseId())) {
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

        changeRepertoryInfo(user, order);
    }

    // 变动库存和生产对应事件
    private void changeRepertoryInfo(User user, RepertoryIn order) {
        //重新拉一次详情信息，因为有些数据保存了修改
        List<RepertoryInDetail> details = repertoryInDetailMapper.getByOrderId(order.getId());
        //把数据插入到库存中
        List<RepertoryInfo> infoList = createRepertoryInfos(user, order, details);
        int inCount = repertoryInfoMapper.insertBatch(infoList);
        logger.info("insert repertory info count:{}", inCount);
        // 生成一个入库信息
        rabbitmqQueueConfig.sendMessage("RepertoryInService", RabbitmqQueueConfig.ORDER_BUY, order);
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
            item.setOnWayQuantity(BigDecimal.ZERO); //初始在单数全部设置为0
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
            item.setRefType(RepertoryRefType.BUY_ORDER.name());
            item.setRefOrderId(order.getId());

            infos.add(item);
        }
        return infos;
    }


    public List<RepertoryInDetail> getInDetailList(RepertoryInListReq reqlist) {

        //根据请求条件对象 获取出库明细
        List<RepertoryInDetail> details = repertoryInDetailMapper.getInDetailList(reqlist);
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        //出库记录需要使用的库存的信息而非商品模板表中的信息
        List<Long> goodsIdList = new ArrayList<>();
        details.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
        List<Goods> goodsList = goodsService.getGoodsById(goodsIdList);
        Map<Long, Goods> goodsMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodsMap.put(item.getId(), item));
        details.stream().forEach(item -> item.setGoods(goodsMap.get(item.getGoodsId())));

        List<Long> inIdList = new ArrayList<>();
        details.stream().forEach(item -> inIdList.add(item.getInOrderId()));
        List<RepertoryIn> repertoryIns = getInListById(inIdList);
        Map<Long, RepertoryIn> repertoryInMap = new HashMap<>();
        repertoryIns.stream().forEach(item -> repertoryInMap.put(item.getId(), item));
        details.stream().forEach(item -> item.setRepertoryIn(repertoryInMap.get(item.getInOrderId())));
        return details;
    }

    public List<RepertoryIn> getInListById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<RepertoryIn> repertoryInList = repertoryInMapper.getInListById(ids);
        return repertoryInList;
    }

    public RepertoryIn getOrderView(Long orderId, User user) throws BizException {
        RepertoryIn repertoryIn = repertoryInMapper.getOrderView(orderId);
        if (repertoryIn == null || !user.getCompanyId().equals(repertoryIn.getCompanyId())) {
            throw new BizException(ErrorCode.RECEIVE_ORDER_GET_FAIL);
        }
        List<RepertoryInDetail> details = getDetailList(repertoryIn);
        repertoryIn.setDetails(details);
        return repertoryIn;
    }
}
