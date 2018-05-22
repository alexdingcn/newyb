package com.yiban.erp.controller.factory;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.FactoryDatabaseMapper;
import com.yiban.erp.entities.FactoryDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/factory/database")
public class FactoryDatabaseController {
    private static final Logger logger = LoggerFactory.getLogger(FactoryDatabaseController.class);

    @Autowired
    private FactoryDatabaseMapper factoryDatabaseMapper;


    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestBody Map requestMap) {
        String companyName = (String) requestMap.get("companyName");
        String permit = (String) requestMap.get("permit");
        String license = (String) requestMap.get("license");
        logger.info("Search factory database, companyName:{}, permit:{}, license:{}", companyName, permit, license);
        List<FactoryDatabase> factoryList = factoryDatabaseMapper.searchByNameOrPermit(companyName, license, permit);
        return ResponseEntity.ok().body(JSON.toJSONString(factoryList));
    }

}