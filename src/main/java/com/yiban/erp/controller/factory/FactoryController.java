package com.yiban.erp.controller.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.FactoryMapper;
import com.yiban.erp.entities.Factory;
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
@RequestMapping("/factory")
public class FactoryController {
    private static final Logger logger = LoggerFactory.getLogger(FactoryController.class);

    @Autowired
    private FactoryMapper factoryMapper;

    /**
     * 获取生产企业的目录树
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestParam(name = "page", required = false) Integer page,
                                       @RequestParam(name = "size", required = false) Integer size,
                                       @RequestParam(name = "search", required = false) String search,
                                       @AuthenticationPrincipal User user) {
        logger.info("get factory list page:{}, size:{}, search：{}", page, size, search);
        Integer pageSize = size == null ? 10 : size;
        Integer offset = (page == null || page <= 0 ? 0 : page - 1) * pageSize;
        if (StringUtils.isBlank(search)) {
            search = null;
        }

        int count = factoryMapper.selectAllCount(user.getCompanyId(), search);
        List<Supplier> supplierList = factoryMapper.selectAllPaged(user.getCompanyId(), search, pageSize, offset);
        JSONObject result = new JSONObject();
        result.put("count", count);
        result.put("data", supplierList);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestBody Map requestMap,
                                         @AuthenticationPrincipal User user) {
        String searchStr = (String) requestMap.get("search");
        if (searchStr != null && !searchStr.isEmpty()) {
            List<Factory> factoryList = factoryMapper.searchByNameOrContact(user.getCompanyId(), searchStr);
            return ResponseEntity.ok().body(JSON.toJSONString(factoryList));
        } else {
            List<Factory> factoryList = factoryMapper.selectAll(user.getCompanyId());
            return ResponseEntity.ok().body(JSON.toJSONString(factoryList));
        }
    }

    @RequestMapping(value = "/{factoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long factoryId) {
        Factory factory = factoryMapper.selectByPrimaryKey(factoryId);
        return ResponseEntity.ok().body(JSON.toJSONString(factory));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String payload,
                                         @AuthenticationPrincipal User user) {
        logger.info("user:{} request remove factory: {}", user.getId(), payload);
        JSONObject requestParams = JSON.parseObject(payload);
        JSONArray factoryIds = requestParams.getJSONArray("ids");
        if (factoryIds != null) {
            for (Object factoryId : factoryIds) {
                int result = factoryMapper.deleteByPrimaryKey(Long.valueOf(String.valueOf(factoryId)));
                if (result <= 0) {
                    logger.error("Failed to delete factoryId=" + factoryId);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody Factory factory,
                                      @AuthenticationPrincipal User user) {
        logger.info("ADD new factory:{} by user:{}", JSON.toJSONString(factory), user.getId());
        int result = 0;
        if (factory.getId() == null) {
            factory.setCompanyId(user.getCompanyId());
            factory.setCreatedBy(user.getNickname());
            factory.setCreatedTime(new Date());
            result = factoryMapper.insertSelective(factory);
        } else {
            factory.setUpdatedBy(user.getNickname());
            factory.setUpdatedTime(new Date());
            result = factoryMapper.updateByPrimaryKeySelective(factory);
        }
        if (result > 0) {
            return ResponseEntity.ok().body(JSON.toJSONString(factory));
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }
}