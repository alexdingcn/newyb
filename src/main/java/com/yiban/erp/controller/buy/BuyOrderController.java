package com.yiban.erp.controller.buy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.BuyOrderStatus;
import com.yiban.erp.constant.ConfigKey;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.BuyOrderDetailMapper;
import com.yiban.erp.dao.BuyOrderMapper;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.dao.SupplierMapper;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.goods.GoodsService;
import com.yiban.erp.service.util.SystemConfigService;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buy")
public class BuyOrderController {
    private static final Logger logger = LoggerFactory.getLogger(BuyOrderController.class);

    @Autowired
    private BuyOrderMapper buyOrderMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BuyOrderDetailMapper buyOrderDetailMapper;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SupplierMapper supplierMapper;


    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user, @RequestBody BuyOrderQuery buyOrderQuery) {
        buyOrderQuery.setCompanyId(user.getCompanyId());
        if (buyOrderQuery.getStartDate() != null) {
            // get start of day
            buyOrderQuery.setStartDate(DateUtils.truncate(buyOrderQuery.getStartDate(), Calendar.DATE));
        }
        if (buyOrderQuery.getEndDate() != null) {
            // get end of day
            buyOrderQuery.setEndDate(DateUtils.truncate(DateUtils.addDays(buyOrderQuery.getEndDate(), 1), Calendar.DATE));
        }
        if (buyOrderQuery.getStatus() == null || buyOrderQuery.getStatus().equals(BuyOrderStatus.ALL)) {
            buyOrderQuery.setStatus(null);
        }
        List<BuyOrder> buyOrderList = buyOrderMapper.queryOrders(buyOrderQuery);
        return ResponseEntity.ok().body(JSON.toJSONString(buyOrderList));
    }

    @RequestMapping(value = "/orderdetail/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user, @PathVariable Long orderId) {
        List<BuyOrderDetail> buyDetailList = buyOrderDetailMapper.findByOrderId(orderId, user.getCompanyId());
        List<Long> goodsIdList = new ArrayList<>();
        if (buyDetailList != null && !buyDetailList.isEmpty()) {
            //嵌入商品信息
            buyDetailList.stream().forEach(item -> goodsIdList.add(item.getGoodsId()));
            List<Goods> goods = goodsService.getGoodsById(goodsIdList);
            //嵌入当前库存信息，最近采购价
            Integer warehouseId = buyDetailList.get(0).getWarehouseId(); //逻辑上同一个订单的仓库ID相同
            List<String> options = Arrays.asList(GoodsQuery.OPTION_LW, GoodsQuery.OPTION_LB, GoodsQuery.OPTION_CBQ);
            goodsService.setGoodsExtra(null, warehouseId, options, goods);

            Map<Long, Goods> goodsMap = new HashMap<>();
            goods.stream().forEach(item -> goodsMap.put(item.getId(), item)); //按ID转Map

            buyDetailList.forEach(item -> item.setGoods(goodsMap.get(item.getGoodsId())));
        }
        return ResponseEntity.ok().body(JSON.toJSONString(buyDetailList));
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateStatus(@AuthenticationPrincipal User user,
                                               @RequestBody BuyOrderQuery query) throws Exception {
        BuyOrder order = buyOrderMapper.selectByPrimaryKey(query.getOrderId());
        if (order == null) {
            throw new BizException(ErrorCode.BUY_ORDER_NOT_EXISTED);
        }
        if (!order.getCompanyId().equals(user.getCompanyId())) {
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        if (!order.canUpdateStatus()) {
            throw new BizException(ErrorCode.BUY_ORDER_IS_CHECKED);
        }
        order.setStatus(query.getOrderStatus());
        order.setCheckBy(user.getNickname());
        order.setCheckResult(query.getCheckResult());
        order.setCheckTime(new Date());
        order.setUpdatedTime(new Date());
        order.setUpdatedBy(user.getNickname());
        int result = buyOrderMapper.updateByPrimaryKeySelective(order);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_UPDATE_FROM_DB.toString());
    }

    @Transactional
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
                                      @RequestBody BuyOrder buyOrder) throws Exception {
        logger.info("ADD new buy order, userId={}", user.getId());
        if (!validateBuyOrder(buyOrder)) {
            throw new BizException(ErrorCode.BUY_ORDER_PARAMS_INVALID);
        }
        Supplier supplier = supplierMapper.selectByPrimaryKey(buyOrder.getSupplierId());
        if (supplier == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //验证下是否符合特殊药品经营管理和冷链经营管理的需求
        validateSpecialManage(supplier, buyOrder);
        validateColdManage(supplier, buyOrder);

        boolean haveCheckFlow = systemConfigService.haveOrderFlow(user.getCompanyId(), ConfigKey.BUY_CHECK);

        int result = 0;
        if (buyOrder.getId() == null) {
            buyOrder.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.BUY));
            buyOrder.setCompanyId(user.getCompanyId());
            buyOrder.setCreatedBy(user.getNickname());
            buyOrder.setStatus(haveCheckFlow ? BuyOrderStatus.INIT : BuyOrderStatus.CHECKED);
            buyOrder.setCreatedTime(new Date());
            buyOrder.setCreatedBy(user.getNickname());
            result = buyOrderMapper.insert(buyOrder);
        } else {
            BuyOrder existingBuyOrder = buyOrderMapper.selectByPrimaryKey(buyOrder.getId());
            if (existingBuyOrder == null) {
                logger.error("Buy order is not existed, query orderId={}", existingBuyOrder.getId());
                throw new BizException(ErrorCode.BUY_ORDER_NOT_EXISTED);
            }
            if (!existingBuyOrder.getCompanyId().equals(user.getCompanyId())) {
                logger.error("Inconsistent company ID, old={} userId={}", existingBuyOrder.getCompanyId(), user.getCompanyId());
                throw new BizException(ErrorCode.ACCESS_PERMISSION);
            }
            if (!existingBuyOrder.canUpdateDetails()) {
                logger.warn("Cannot update order status, already checked, buyOrderId={}", buyOrder.getId());
                throw new BizException(ErrorCode.BUY_ORDER_IS_CHECKED);
            }

            buyOrder.setUpdatedBy(user.getNickname());
            buyOrder.setUpdatedTime(new Date());

            // add company id in query criteria to avoid hack, or we can use order uuid
            buyOrder.setCompanyId(user.getCompanyId());
            buyOrder.setOrderNumber(existingBuyOrder.getOrderNumber());
            // status update to INIT
            buyOrder.setStatus(haveCheckFlow ? BuyOrderStatus.INIT : BuyOrderStatus.CHECKED);
            result = buyOrderMapper.updateByPrimaryKeySelective(buyOrder);
        }

        if (result > 0) {
            // save order details
            saveOrderDetails(buyOrder, user);
            JSONObject obj = new JSONObject();
            obj.put("orderId", buyOrder.getId());
            obj.put("status", buyOrder.getStatus());
            return ResponseEntity.ok().body(obj.toString());
        }
        throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
    }

    private void validateSpecialManage(Supplier supplier, BuyOrder buyOrder) throws BizException {

        //如果存在空的数据，需要验证是否存在有冷链经营性商品，如果有，返回验证不通过
        List<Long> goodsIds = new ArrayList<>();
        for (BuyOrderDetail detail : buyOrder.getDetails()) {
            goodsIds.add(detail.getGoodsId());
        }
        //查询是否存在这些商品ID中是否存在冷链经营类型的商品
        boolean haveCold = goodsService.haveSpecialManageGoods(goodsIds);
        if (!haveCold) {
            return;
        }
        //验证供应商是否有改资质
        if (supplier.getCanSpecial() == null || !supplier.getCanSpecial()) {
            logger.warn("supplier can not cold manage. supplier:{}", supplier.getId());
            throw new BizException(ErrorCode.RECEIVE_SUPPLIER_SPECIAL_VALIDATE_FAIL);
        }
    }

    private void validateColdManage(Supplier supplier, BuyOrder buyOrder) throws BizException {
        //如果存在空的数据，需要验证是否存在有冷链经营性商品，如果有，返回验证不通过
        List<Long> goodsIds = new ArrayList<>();
        for (BuyOrderDetail detail : buyOrder.getDetails()) {
            goodsIds.add(detail.getGoodsId());
        }
        //查询是否存在这些商品ID中是否存在冷链经营类型的商品
        boolean haveCold = goodsService.haveColdManageGoods(goodsIds);
        if (!haveCold) {
            return;
        }
        //验证供应商是否有改资质
        if (supplier.getColdBusiness() == null || !supplier.getColdBusiness()) {
            logger.warn("supplier can not cold manage. supplier:{}", supplier.getId());
            throw new BizException(ErrorCode.RECEIVE_SUPPLIER_COLD_VALIDATE_FAIL);
        }

        //先看下订单的温控方式和运输方式是否都已经输入了，如果都输入了，没必要再验证
        if (buyOrder.getTemperControlId() == null || buyOrder.getTemperControlId() <= 0
                || buyOrder.getShipMethodId() == null || buyOrder.getShipMethodId() <= 0) {
            throw new BizException(ErrorCode.BUY_ORDER_COLD_MANAGE_ERROR);
        }
    }

    private boolean validateBuyOrder(BuyOrder buyOrder) {
        if (buyOrder == null) {
            return false;
        }
        if (buyOrder.getDetails() == null || buyOrder.getDetails().isEmpty()) {
            return false;
        }
        if (buyOrder.getSupplierId() == null) {
            return false;
        }
        return true;
    }

    @Transactional
    public int saveOrderDetails(BuyOrder buyOrder, User operator) {
        int result = 0;
        // remove existing details
        if (buyOrder != null && buyOrder.getId() != null && buyOrder.getId() > 0) {
            buyOrderDetailMapper.deleteByBuyOrderId(buyOrder.getId()); //直接做全部删除后在做添加

            if (buyOrder.getDetails() != null && !buyOrder.getDetails().isEmpty()) {
                for (BuyOrderDetail detail : buyOrder.getDetails()) {
                    detail.setCreatedBy(operator.getNickname());
                    detail.setCreatedTime(new Date());
                    detail.setBuyOrderId(buyOrder.getId());
                    detail.setShippedQuantity(BigDecimal.ZERO);
                    detail.setAlreadyFapiao(false);
                    result += buyOrderDetailMapper.insert(detail);
                }
            }
        }
        return result;
    }

    // 删除采购单
    @RequestMapping(value = "/remove/{buyOrderId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeBuyOrder(@PathVariable Long buyOrderId,
                                                 @AuthenticationPrincipal User user) throws Exception {

        logger.info("user:{} request to remove buy order:{}", user.getId(), buyOrderId);
        //先查询，并且获取到对应的状态，只有在未审核之前的状态才能删除
        BuyOrder buyOrder = buyOrderMapper.selectByPrimaryKey(buyOrderId);
        if (buyOrder == null || !user.getCompanyId().equals(buyOrder.getCompanyId())) {
            logger.warn("get buy order fail by id:{}", buyOrderId);
            throw new BizException(ErrorCode.BUY_ORDER_NOT_EXISTED);
        }
        if (!buyOrder.canUpdateDetails()) {
            // 状态已经审核过了，或者其他不能修改的状态，不能删除
            throw new BizException(ErrorCode.BUY_ORDER_STATUS_CANNOT_REMOVE);
        }
        //如果能验证通过，直接把订单修改到DELETE状态
        buyOrder.setStatus(BuyOrderStatus.DELETE);
        buyOrder.setUpdatedBy(user.getNickname());
        buyOrder.setUpdatedTime(new Date());
        buyOrderMapper.updateByPrimaryKeySelective(buyOrder);
        return ResponseEntity.ok().build();
    }

}
