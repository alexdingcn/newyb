package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.RepertoryInMapper;
import com.yiban.erp.entities.RepertoryIn;
import com.yiban.erp.entities.User;
import com.yiban.erp.dto.InOrderQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repertory/in")
public class RepertoryInController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryInController.class);


    @Autowired
    private RepertoryInMapper repertoryInMapper;

    //根据条件获取入库单list
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                       @RequestBody InOrderQuery inOrderQuery) {
        inOrderQuery.setCompanyId(user.getCompanyId());
        List<RepertoryIn> list = repertoryInMapper.queryInOrders(inOrderQuery);
        JSONObject JSON = new JSONObject();
        return ResponseEntity.ok().body(JSON.toJSONString(list));
    }
}
