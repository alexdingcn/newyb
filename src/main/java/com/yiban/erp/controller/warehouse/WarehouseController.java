package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.WarehouseMapper;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);

    @Autowired
    private WarehouseMapper warehouseMapper;

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
    public ResponseEntity<String> remove(@RequestBody String payload) {
        JSONObject requestParams = JSON.parseObject(payload);
        JSONArray warehouseIds = requestParams.getJSONArray("ids");
        if (warehouseIds != null) {
            for (Object warehouseId : warehouseIds) {
                int result = warehouseMapper.deleteByPrimaryKey(Integer.valueOf(String.valueOf(warehouseId)));
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
}
