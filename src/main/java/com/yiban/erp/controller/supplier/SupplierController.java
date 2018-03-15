package com.yiban.erp.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.SupplierMapper;
import com.yiban.erp.entities.Supplier;
import com.yiban.erp.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/supplier")
public class SupplierController {
    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    @Autowired
    private SupplierMapper supplierMapper;

    /**
     * 获取供应商的目录树
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list() {
        List<Supplier> supplierList = supplierMapper.selectAll();
        return ResponseEntity.ok().body(JSON.toJSONString(supplierList));
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestBody Map requestMap) {
        String searchStr = (String) requestMap.get("search");
        if (StringUtils.isNotEmpty(searchStr)) {
            List<Supplier> supplierList = supplierMapper.searchByNameOrContact(searchStr);
            return ResponseEntity.ok().body(JSON.toJSONString(supplierList));
        } else {
            return list();
        }
    }

    @RequestMapping(value = "/{supplierId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long supplierId) {
        Supplier supplier = supplierMapper.selectByPrimaryKey(supplierId);
        return ResponseEntity.ok().body(JSON.toJSONString(supplier));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String payload) {
        JSONObject requestParams = JSON.parseObject(payload);
        JSONArray supplierIds = requestParams.getJSONArray("ids");
        if (supplierIds != null) {
            for (Object supplierId : supplierIds) {
                int result = supplierMapper.deleteByPrimaryKey(Long.valueOf(String.valueOf(supplierId)));
                if (result <= 0) {
                    logger.error("Failed to delete supplierId=" + supplierId);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
                                      @RequestBody Supplier supplier) {
        logger.info("ADD new supplier:{}", supplier.getName());

        int result = 0;
        if (supplier.getId() == null) {
            supplier.setCreatedBy(user.getNickname());
            supplier.setCreatedTime(new Date());
            result = supplierMapper.insertSelective(supplier);
        } else {
            supplier.setUpdatedBy(user.getNickname());
            supplier.setUpdatedTime(new Date());
            result = supplierMapper.updateByPrimaryKey(supplier);
        }
        if (result > 0) {
            return ResponseEntity.ok().body(supplier.getId().toString());
        }
        return ResponseEntity.badRequest().body("Failed to insert/update");
    }
}