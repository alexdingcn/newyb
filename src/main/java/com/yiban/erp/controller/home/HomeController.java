package com.yiban.erp.controller.home;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.SellOrderMapper;
import com.yiban.erp.entities.StatusCount;
import com.yiban.erp.entities.User;
import org.apache.http.client.utils.DateUtils;
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
import java.util.Map;

@RestController
@RequestMapping("/home")

public class HomeController {
    @Autowired
    private SellOrderMapper sellOrderMapper;

    @RequestMapping(value = "/orderstats", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getOrderStats(@AuthenticationPrincipal User user) {

        List<StatusCount> counts = sellOrderMapper.getOrderStatusStat(user.getCompanyId());

        return ResponseEntity.ok().body(JSON.toJSONString(counts));
    }

    @RequestMapping(value = "/amountstats", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getAmountStat(@AuthenticationPrincipal User user, @RequestBody Map requestMap) {
        Date startDate = DateUtils.parseDate((String) requestMap.getOrDefault("startDate", ""), new String[]{"yyyy-MM-dd"});
        Date endDate = DateUtils.parseDate((String) requestMap.getOrDefault("endDate", ""), new String[]{"yyyy-MM-dd"});

        List<StatusCount> counts = sellOrderMapper.getOrderAmountStat(user.getCompanyId(), startDate, endDate);

        return ResponseEntity.ok().body(JSON.toJSONString(counts));
    }
}
