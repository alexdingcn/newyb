package com.yiban.erp.service.warehouse;

import com.yiban.erp.constant.WarehouseStatus;
import com.yiban.erp.dao.WarehouseMapper;
import com.yiban.erp.entities.Warehouse;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WarehouseService {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    @Autowired
    private WarehouseMapper warehouseMapper;

    /**
     * 判断一个仓库是否正在盘库
     * @param warehouseId
     * @return
     */
    public boolean isFrozen(Integer warehouseId) throws BizException {
        Warehouse warehouse = warehouseMapper.selectByPrimaryKey(warehouseId);
        if (warehouse == null) {
            logger.error("order get warehouse fail.");
            throw new BizException(ErrorCode.WAREHOUSE_GET_FAIL);
        }
        if (!WarehouseStatus.NORMAL.name().equalsIgnoreCase(warehouse.getStatus())) {
            logger.warn("warehouse is frozen now, status is not normal. warehouseId:{} status:{}", warehouse.getId(), warehouse.getStatus());
            return false;
        }else {
            return true;
        }
    }

}
