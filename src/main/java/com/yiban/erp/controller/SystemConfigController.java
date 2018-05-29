package com.yiban.erp.controller;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.entities.SystemConfig;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.util.SystemConfigService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/config")
public class SystemConfigController {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigController.class);

    @Autowired
    private SystemConfigService systemConfigService;


    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> configList(@AuthenticationPrincipal User user) throws Exception {
        Map<String, SystemConfig> result = systemConfigService.getConfigMap(user.getCompanyId());
        return ResponseEntity.ok().body((JSON) JSON.toJSON(result));
    }

    @RequestMapping(value = "/save/one", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveOne(@RequestBody SystemConfig config,
                                          @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save one config:{}", user.getId(), JSON.toJSONString(config));
        systemConfigService.saveOne(config, user, true);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/save/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveList(@RequestBody List<SystemConfig> configs,
                                           @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save config list:{}", user.getId(), JSON.toJSONString(configs));
        systemConfigService.saveList(configs, user);
        return ResponseEntity.ok().build();
    }



}
