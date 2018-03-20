package com.yiban.erp.controller.ship;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.ShipCompanyMapper;
import com.yiban.erp.entities.ShipCompany;
import com.yiban.erp.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/ship")
@RestController
public class ShipCompanyController {

    private static final Logger logger = LoggerFactory.getLogger(ShipCompanyController.class);

    @Autowired
    private ShipCompanyMapper shipCompanyMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user) {
        List<ShipCompany> shipCompanies = shipCompanyMapper.getByCompanyId(user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(shipCompanies));
    }

}
