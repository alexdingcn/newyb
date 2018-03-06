package com.yiban.erp.controller.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.FactoryMapper;
import com.yiban.erp.entities.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> list() {
        List<Factory> factoryList = factoryMapper.selectAll();
        return ResponseEntity.ok().body(JSON.toJSONString(factoryList));
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestBody Map requestMap) {
        String searchStr = (String) requestMap.get("search");
        if (!searchStr.isEmpty()) {
            List<Factory> factoryList = factoryMapper.searchByNameOrContact(searchStr);
            return ResponseEntity.ok().body(JSON.toJSONString(factoryList));
        } else {
            return list();
        }
    }

    @RequestMapping(value = "/{factoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long factoryId) {
        Factory factory = factoryMapper.selectByPrimaryKey(factoryId);
        return ResponseEntity.ok().body(JSON.toJSONString(factory));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@RequestBody String payload) {
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

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody Factory factory) {
        logger.info("ADD new factory:{}", factory);
        factory.setCreatedBy("admin");
        factory.setCreatedTime(new Date());
        int result = 0;
        if (factory.getId() == null) {
            result = factoryMapper.insertSelective(factory);
        } else {
            result = factoryMapper.updateByPrimaryKey(factory);
        }
        if (result > 0) {
            return ResponseEntity.ok().body(factory.getId().toString());
        }
        return ResponseEntity.badRequest().body("Failed to insert/update");
    }
}