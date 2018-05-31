package com.yiban.erpcustomer.controller.home;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erpcustomer.dao.BannerMapper;
import com.yiban.erpcustomer.dto.PagedQuery;
import com.yiban.erpcustomer.entities.Banner;
import com.yiban.erpcustomer.entities.User;
import com.yiban.erpcustomer.exception.ErrorCode;
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

@RestController
@RequestMapping("/home/banner")
public class HomeBannerController {
    private static final Logger logger = LoggerFactory.getLogger(HomeBannerController.class);

    @Autowired
    private BannerMapper bannerMapper;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<JSON> getBannerList(@RequestBody PagedQuery query,
                                               @AuthenticationPrincipal User user) {
        query.setCompanyId(user.getCompanyId());
        Integer count = bannerMapper.selectAllCount(query);
        JSONObject obj = new JSONObject();
        if (count != null && count > 0) {
            List<Banner> banners = bannerMapper.selectAll(query);
            obj.put("data", banners);
            obj.put("count", count);
            return ResponseEntity.ok().body(obj);
        }
        obj.put("data", Collections.emptyList());
        obj.put("count", 0);
        return ResponseEntity.ok().body(obj);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> addBanner(@RequestBody Banner banner,
                                             @AuthenticationPrincipal User user) {
        int result = 0;
        if (banner.getId() != null) {
            banner.setUpdatedBy(user.getId().toString());
            banner.setUpdatedTime(new Date());
            result = bannerMapper.updateByPrimaryKey(banner);
        } else {
            banner.setCompanyId(user.getCompanyId());
            banner.setCreatedBy(user.getId().toString());
            banner.setCreatedTime(new Date());
            result = bannerMapper.insert(banner);
        }
        if (result > 0) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }


    @RequestMapping(value = "/{bannerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> deleteBanner(@PathVariable Long bannerId,
                                                @AuthenticationPrincipal User user) {
        logger.info("user:{} request remove banner id:{}", user.getId(), bannerId);
        Banner banner = bannerMapper.selectByPrimaryKey(bannerId);
        if (banner == null) {
            return ResponseEntity.badRequest().body(ErrorCode.BANNER_NOT_EXISTED.toString());
        }
        if (!user.getCompanyId().equals(banner.getCompanyId())) {
            return ResponseEntity.badRequest().body(ErrorCode.ACCESS_PERMISSION.toString());
        }

        int result = bannerMapper.deleteByPrimaryKey(banner.getId());
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_DELETE_FROM_DB.toString());
    }
}
