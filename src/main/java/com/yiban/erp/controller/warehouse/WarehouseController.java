package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.WarehouseLocationStatus;
import com.yiban.erp.constant.WarehouseStatus;
import com.yiban.erp.dao.WarehouseLocationMapper;
import com.yiban.erp.dao.WarehouseMapper;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.Warehouse;
import com.yiban.erp.entities.WarehouseLocation;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);

    @Autowired
    private WarehouseMapper warehouseMapper;
    @Autowired
    private WarehouseLocationMapper warehouseLocationMapper;

    /**
     * 获取仓库列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user) {
        List<Warehouse> warehouseList = warehouseMapper.getWarehouses(user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(warehouseList));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String payload,
                                         @AuthenticationPrincipal User user) {
        JSONObject requestParams = JSON.parseObject(payload);
        JSONArray warehouseIds = requestParams.getJSONArray("ids");
        if (warehouseIds != null) {
            for (Object warehouseId : warehouseIds) {
                int result = warehouseMapper.updateStatus(Integer.valueOf(String.valueOf(warehouseId)),
                        WarehouseStatus.DELETE.name(), user.getNickname(), new Date());
                if (result <= 0) {
                    logger.error("Failed to delete warehouseId=" + warehouseId);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
                                      @RequestBody Warehouse warehouse) {
        logger.info("ADD new warehouse, userId={}, warehouse={}", user.getId(), warehouse.getName());

        int result = 0;
        if (warehouse.getId() == null) {
            warehouse.setCreatedBy(user.getNickname());
            warehouse.setCreatedTime(new Date());
            warehouse.setCompanyId(user.getCompanyId());
            warehouse.setStatus(WarehouseStatus.NORMAL.name());
            result = warehouseMapper.insertSelective(warehouse);
        } else {
            warehouse.setUpdatedBy(user.getNickname());
            warehouse.setUpdatedTime(new Date());
            result = warehouseMapper.updateByPrimaryKey(warehouse);
        }
        if (result > 0) {
            return ResponseEntity.ok().body(warehouse.getId().toString());
        }
        return ResponseEntity.badRequest().body("Failed to insert/update");
    }

    @RequestMapping(value = "/location/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLocationList(@RequestParam("warehouseId") Integer warehouseId) {
        List<WarehouseLocation> locations = warehouseLocationMapper.getList(warehouseId);
        return ResponseEntity.ok().body(JSON.toJSONString(locations));
    }

    @RequestMapping(value = "/location/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addLocation(@RequestBody WarehouseLocation location,
                                              @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} add warehouse location location:{}", user.getId(), location.getLocation());
        if (StringUtils.isBlank(location.getLocation()) || location.getWarehouseId() == null) {
            logger.warn("add location but location value is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //验证下当前location是否意见在对应仓库下存在了，如果存在了，不给添加
        WarehouseLocation existLocation = warehouseLocationMapper.getByLocation(location.getWarehouseId(), location.getLocation());
        if (existLocation != null) {
            logger.warn("location:{} is exist in warehouse:{}", location.getLocation(), location.getWarehouseId());
            throw new BizException(ErrorCode.WAREHOUSE_LOCATION_EXIST);
        }
        if (location.getId() == null) {
            location.setStatus(WarehouseLocationStatus.NORMAL.name());
            location.setCreateBy(user.getNickname());
            location.setCreateTime(new Date());
            warehouseLocationMapper.insert(location);
        }else {
            location.setUpdateBy(user.getNickname());
            location.setUpdateTime(new Date());
            warehouseLocationMapper.updateSelective(location);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/location/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeLocation(@RequestBody String payload,
                                         @AuthenticationPrincipal User user) {
        JSONObject requestParams = JSON.parseObject(payload);
        String locationIds = requestParams.getString("ids");
        if (StringUtils.isNotBlank(locationIds)) {
            List<Integer> ids = JSON.parseArray(locationIds, Integer.class);
            List<WarehouseLocation> locations = warehouseLocationMapper.getListByIds(ids);
            for (WarehouseLocation item: locations) {
                item.setUpdateBy(user.getNickname());
                item.setUpdateTime(new Date());
                item.setStatus(WarehouseLocationStatus.DELETE.name());
                int result = warehouseLocationMapper.updateSelective(item);
                if (result <= 0) {
                    logger.error("Failed to delete locationId=" + item.getId());
                }
            }
        }
        return ResponseEntity.ok().build();
    }

}
