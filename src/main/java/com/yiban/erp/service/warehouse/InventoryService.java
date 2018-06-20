package com.yiban.erp.service.warehouse;

import com.yiban.erp.constant.InventoryStatus;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.constant.WarehouseStatus;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.dao.RepertoryInventoryDetailMapper;
import com.yiban.erp.dao.RepertoryInventoryMapper;
import com.yiban.erp.dao.WarehouseMapper;
import com.yiban.erp.dto.InventorySearch;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.RepertoryInventory;
import com.yiban.erp.entities.RepertoryInventoryDetail;
import com.yiban.erp.entities.User;
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
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private RepertoryInventoryMapper inventoryMapper;
    @Autowired
    private RepertoryInventoryDetailMapper inventoryDetailMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;

    /**
     * 获取盘点单列表
     * @param search
     * @return
     */
    public List<RepertoryInventory> getOrderList(InventorySearch search) {
        return inventoryMapper.getOrderList(search);
    }

    public int getOrderListCount(InventorySearch search) {
        Integer result = inventoryMapper.getOrderListCount(search);
        if (result == null || result < 0) {
            return 0;
        }else {
            return result;
        }
    }

    public List<RepertoryInventoryDetail> getDetails(Long inventoryId) {
        List<RepertoryInventoryDetail> details = inventoryDetailMapper.getDetails(inventoryId);
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

    @Transactional
    public void cancelOrder(Long inventoryId, User user) throws BizException {
        RepertoryInventory inventory = inventoryMapper.selectByPrimaryKey(inventoryId);
        if (inventory == null || !user.getCompanyId().equals(inventory.getCompanyId())) {
            logger.warn("get inventory order fail by id:{}", inventoryId);
            throw new BizException(ErrorCode.INVENTORY_ORDER_GET_FAIL);
        }
        if (!InventoryStatus.UNCHECK.name().equalsIgnoreCase(inventory.getStatus())) {
            logger.warn("inventory:{} current status:{} can not do cancel.", inventory.getId(), inventory.getStatus());
            throw new BizException(ErrorCode.INVENTORY_STATUS_CANNOT_CANCEL);
        }
        inventory.setStatus(InventoryStatus.CANCEL.name());
        inventory.setUpdatedBy(user.getNickname());
        inventory.setUpdatedTime(new Date());
        inventoryMapper.updateByPrimaryKeySelective(inventory);

        //如果当前仓库下没有其他正在UNCHECK的盘点单，需要把当前仓库的状态修改为正常状态
        int uncheckCount = inventoryMapper.warehouseHaveUncheckCount(inventory.getWarehouseId());
        if (uncheckCount <= 0) {
            logger.info("warehouse have not UNCHECK inventory order then release warehouse status to NORMAL.");
            warehouseMapper.updateStatus(inventory.getWarehouseId(), WarehouseStatus.NORMAL.name(), user.getNickname(), new Date());
        }
    }


    public void saveInventory(RepertoryInventory inventory, User user) throws BizException {
        //验证下必输条件
        if (StringUtils.isEmpty(inventory.getOrderName()) || inventory.getWarehouseId() == null
                || inventory.getDetails() == null || inventory.getDetails().isEmpty()) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //如果有Id,认为是修改，如果没有，认为是新增
        if (inventory.getId() == null || inventory.getId() <= 0) {
            addInventory(inventory, user);
        }else {
            updateInventory(inventory, user);
        }
    }

    @Transactional
    public void addInventory(RepertoryInventory inventory, User user) throws BizException {
        inventory.setCompanyId(user.getCompanyId());
        inventory.setStatus(InventoryStatus.UNCHECK.name());
        inventory.setOrderNumber(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.INVENTORY));
        inventory.setCreatedBy(user.getNickname());
        inventory.setCreatedTime(new Date());
        int count = inventoryMapper.insert(inventory);
        if (count <= 0 || inventory.getId() == null || inventory.getId() <= 0) {
            logger.warn("insert invent order fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("insert inventory order success, id:{}", inventory.getId());
        List<RepertoryInventoryDetail> details = makeInsertDetails(inventory.getDetails(), inventory, user);
        int detailCount = inventoryDetailMapper.insertBatch(details);
        logger.info("insert inventory details success count:{}", detailCount);

        //把当前仓库点的状态修改为冻结状态
        warehouseMapper.updateStatus(inventory.getWarehouseId(), WarehouseStatus.FROZEN.name(), user.getNickname(), new Date());
    }

    @Transactional
    public void updateInventory(RepertoryInventory inventory, User user) throws BizException {
        //先查询下原来的，看看状态，只有在UNCHECK状态下才能修改
        RepertoryInventory old = inventoryMapper.selectByPrimaryKey(inventory.getId());
        if (old == null || !user.getCompanyId().equals(old.getCompanyId())) {
            throw new BizException(ErrorCode.INVENTORY_ORDER_GET_FAIL);
        }
        if (!InventoryStatus.UNCHECK.name().equalsIgnoreCase(old.getStatus())) {
            throw new BizException(ErrorCode.INVENTORY_STATUS_CANNOT_UPDATE);
        }

        inventory.setCompanyId(user.getCompanyId());
        inventory.setUpdatedTime(new Date());
        inventory.setUpdatedBy(user.getNickname());
        inventoryMapper.updateByPrimaryKeySelective(inventory);

        logger.info("inventory update save success then update details. id:{}", inventory.getId());
        //先删除原来的详情，然后插入新的
        inventoryDetailMapper.deleteByInventoryId(inventory.getId());
        List<RepertoryInventoryDetail> details = makeInsertDetails(inventory.getDetails(), inventory, user);
        int detailCount = inventoryDetailMapper.insertBatch(details);
        logger.info("update inventory details success count:{}", detailCount);
    }

    private List<RepertoryInventoryDetail> makeInsertDetails(List<RepertoryInventoryDetail> details, RepertoryInventory inventory, User user) {
        //新建详情
        for (RepertoryInventoryDetail detail : details) {
            detail.setInventoryId(inventory.getId());
            BigDecimal changeQuantity = BigDecimal.ZERO;
            BigDecimal repertoryQuantity = detail.getRepertoryQuantity() == null ? BigDecimal.ZERO : detail.getRepertoryQuantity();
            BigDecimal inventoryQuantity = detail.getInventoryQuantity() == null ? BigDecimal.ZERO : detail.getInventoryQuantity();
            changeQuantity = inventoryQuantity.subtract(repertoryQuantity);
            detail.setChangeQuantity(changeQuantity);
            detail.setCreatedBy(user.getNickname());
            detail.setCreatedTime(new Date());
        }
        return details;
    }

    public RepertoryInventory getView(Long inventoryId, User user) throws BizException {
        RepertoryInventory inventory = inventoryMapper.getInventory(inventoryId);
        if (inventory == null || !user.getCompanyId().equals(inventory.getCompanyId())) {
            throw new BizException(ErrorCode.INVENTORY_ORDER_GET_FAIL);
        }
        List<RepertoryInventoryDetail> details = getDetails(inventoryId);
        inventory.setDetails(details);
        return inventory;
    }

    @Transactional
    public void check(Long inventoryId, User user) throws BizException {
        RepertoryInventory inventory = inventoryMapper.selectByPrimaryKey(inventoryId);
        if (inventory == null || !user.getCompanyId().equals(inventory.getCompanyId())) {
            throw new BizException(ErrorCode.INVENTORY_ORDER_GET_FAIL);
        }
        if (!InventoryStatus.UNCHECK.name().equalsIgnoreCase(inventory.getStatus())) {
            logger.warn("inventory:{} current status:{} can not do check.", inventory.getId(), inventory.getStatus());
            throw new BizException(ErrorCode.INVENTORY_STATUS_CANNOT_CHECK);
        }
        //修改状态
        inventory.setCheckUser(user.getId());
        inventory.setCheckTime(new Date());
        inventory.setStatus(InventoryStatus.CHECKED.name());
        inventory.setUpdatedBy(user.getNickname());
        inventory.setUpdatedTime(new Date());
        int count = inventoryMapper.updateByPrimaryKeySelective(inventory);
        if (count <= 0 ) {
            logger.warn("inventory:{} check update inventory fail.", inventory.getId());
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        logger.info("inventory:{} check update record success, then update repertory info by inventory.", inventory.getId());
        int repertoryCount = repertoryInfoMapper.inventoryChangeQuantity(inventory.getId());
        logger.info("repertory info update count:{} by inventory id:{}", repertoryCount, inventory.getId());

        //如果当前仓库下没有其他正在UNCHECK的盘点单，需要把当前仓库的状态修改为正常状态
        int uncheckCount = inventoryMapper.warehouseHaveUncheckCount(inventory.getWarehouseId());
        if (uncheckCount <= 0) {
            logger.info("warehouse have not UNCHECK inventory order then release warehouse status to NORMAL.");
            warehouseMapper.updateStatus(inventory.getWarehouseId(), WarehouseStatus.NORMAL.name(), user.getNickname(), new Date());
        }
    }
}
