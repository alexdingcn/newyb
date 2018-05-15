package com.yiban.erp.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.SupplierMapper;
import com.yiban.erp.entities.Supplier;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
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
    public ResponseEntity<String> list(@RequestParam(name = "page", required = false) Integer page,
                                       @RequestParam(name = "size", required = false) Integer size,
                                       @RequestParam(name = "search", required = false) String search,
                                       @AuthenticationPrincipal User user) {
        logger.info("get supplier list page:{}, size:{}, search：{}", page, size, search);
        Integer pageSize = size == null ? 10 : size;
        Integer offset = (page == null || page <= 0 ? 0 : page - 1) * pageSize;
        if (StringUtils.isBlank(search)) {
            search = null;
        }

        int count = supplierMapper.selectAllCount(user.getCompanyId(), search);
        List<Supplier> supplierList = supplierMapper.selectAllPaged(user.getCompanyId(), search, pageSize, offset);
        JSONObject result = new JSONObject();
        result.put("count", count);
        result.put("data", supplierList);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestBody Map requestMap,
                                         @AuthenticationPrincipal User user) {
        String searchStr = (String) requestMap.get("search");
        if (StringUtils.isNotEmpty(searchStr)) {
            List<Supplier> supplierList = supplierMapper.searchByNameOrContact(user.getCompanyId(), searchStr);
            return ResponseEntity.ok().body(JSON.toJSONString(supplierList));
        } else {
            List<Supplier> supplierList = supplierMapper.selectAll(user.getCompanyId());
            return ResponseEntity.ok().body(JSON.toJSONString(supplierList));
        }
    }

    @RequestMapping(value = "/{supplierId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long supplierId) {
        Supplier supplier = supplierMapper.selectByPrimaryKey(supplierId);
        return ResponseEntity.ok().body(JSON.toJSONString(supplier));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String payload,
                                         @AuthenticationPrincipal User user) {
        logger.info("user:{} request to remove supplier:{}", user.getId(), payload);
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

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@AuthenticationPrincipal User user,
                                      @RequestBody Supplier supplier) {
        logger.info("ADD new supplier:{}", supplier.getName());
        int result = 0;
        if (supplier.getId() == null) {
            supplier.setCompanyId(user.getCompanyId());
            supplier.setCreatedBy(user.getNickname());
            supplier.setCreatedTime(new Date());
            result = supplierMapper.insert(supplier);
        } else {
            supplier.setUpdatedBy(user.getNickname());
            supplier.setUpdatedTime(new Date());
            result = supplierMapper.updateByPrimaryKeySelective(supplier);
        }
        if (result > 0) {
            return ResponseEntity.ok().body(JSON.toJSONString(supplier));
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }
}