package com.yiban.erp.controller.ship;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.ShipCompanyMapper;
import com.yiban.erp.entities.ShipCompany;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequestMapping("/ship")
@RestController
public class ShipCompanyController {

    private static final Logger logger = LoggerFactory.getLogger(ShipCompanyController.class);

    @Autowired
    private ShipCompanyMapper shipCompanyMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestParam(name = "search", required = false) String search,
                                       @RequestParam(name = "page", required = false) Integer page,
                                       @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                       @AuthenticationPrincipal User user) {
        Integer limit = pageSize == null || pageSize <= 0 ? null : pageSize;
        Integer offset = (pageSize != null && page != null && page > 0) ? (page -1) * limit : null;
        String searchVal = StringUtils.isBlank(search) ? null : search;

        List<ShipCompany> shipCompanies = Collections.emptyList();
        int count = shipCompanyMapper.getListCount(user.getCompanyId(), searchVal);
        if (count > 0) {
            shipCompanies = shipCompanyMapper.getList(user.getCompanyId(), searchVal, offset, limit);
        }
        JSONObject response = new JSONObject();
        response.put("data", shipCompanies);
        response.put("count", count);
        return ResponseEntity.ok().body(response.toJSONString());
    }

    @RequestMapping(value = "/{shipId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getById(@PathVariable Integer shipId) {
        ShipCompany shipCompany = shipCompanyMapper.selectByPrimaryKey(shipId);
        return ResponseEntity.ok().body(JSON.toJSONString(shipCompany));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody ShipCompany shipCompany,
                                       @AuthenticationPrincipal User user) throws Exception {
        if (shipCompany == null || shipCompany.getName() == null) {
            logger.warn("user:{} save ship company info but params is error.");
            throw new BizException(ErrorCode.SHIP_SAVE_PARAMS_ERROR);
        }
        if (shipCompany.getId() == null) {
            logger.info("user:{} create a ship company info:{}", user.getId(), JSON.toJSONString(shipCompany));
            shipCompany.setCompanyId(user.getCompanyId());
            shipCompany.setCreateBy(user.getNickname());
            shipCompany.setCreateTime(new Date());
            shipCompanyMapper.insert(shipCompany);
        }else {
            logger.info("user:{} update a ship company info:{}", user.getId(), JSON.toJSONString(shipCompany));
            shipCompany.setUpdateBy(user.getNickname());
            shipCompany.setUpdateTime(new Date());
            shipCompanyMapper.updateByPrimaryKeySelective(shipCompany);
        }
        return ResponseEntity.ok().body(JSON.toJSONString(shipCompany));
    }

}
