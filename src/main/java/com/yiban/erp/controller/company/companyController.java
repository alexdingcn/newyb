package com.yiban.erp.controller.company;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.entities.Company;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.company.companyService;
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

@RestController
@RequestMapping("/company")
public class companyController {
    private static final Logger logger = LoggerFactory.getLogger(companyController.class);

    @Autowired
    private companyService companyservice;

    @RequestMapping(value="/initInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> initInfo(@AuthenticationPrincipal User user) throws Exception{
        if (user == null) {
            throw new BizException(ErrorCode.USER_GET_FAIL);
        }
        Company company = companyservice.initCompany(user.getId());
        return ResponseEntity.ok().body(JSON.toJSONString(company));
    }

    @RequestMapping(value = "/updateCompany",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCompany(@RequestBody Company company )throws Exception{
        logger.info("ADD new factory:{} by user:{}", JSON.toJSONString(company));
        if (company == null) {
            throw new BizException(ErrorCode.SHIP_SAVE_PARAMS_ERROR);
        }
        int i = companyservice.updateCompany(company);
        if (i > 0){
            return ResponseEntity.ok().build();
        }else{
            throw new BizException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }

    }
}
