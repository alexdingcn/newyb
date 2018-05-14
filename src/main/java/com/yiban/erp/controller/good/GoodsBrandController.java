package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodsBrandMapper;
import com.yiban.erp.dto.GoodsBrandQuery;
import com.yiban.erp.entities.GoodsBrand;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.goods.GoodsBrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/goods/brand")
public class GoodsBrandController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsBrandController.class);

    @Autowired
    private GoodsBrandService goodsBrandService;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestBody GoodsBrandQuery query,
                                       @AuthenticationPrincipal User user) throws Exception {
        query.setCompanyId(user.getCompanyId());
        int count = goodsBrandService.getBrandCount(query);
        List<GoodsBrand> brands = new ArrayList<>();
        if (count > 0) {
            brands = goodsBrandService.getBrands(query);
        }
        JSONObject result = new JSONObject();
        result.put("count", count);
        result.put("data", brands);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody GoodsBrand goodsBrand,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save goodsBrand:{}", user.getId(), JSON.toJSONString(goodsBrand));
        goodsBrandService.save(goodsBrand, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/delete/{brandId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Long brandId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove goods brand:{}", user.getId(), brandId);
        goodsBrandService.remove(brandId);
        return ResponseEntity.ok().build();
    }

}
