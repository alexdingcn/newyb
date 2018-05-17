package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.GoodsBlackListMapper;
import com.yiban.erp.entities.GoodsBlackListWithBLOBs;
import com.yiban.erp.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/goods/blacklist")
public class GoodsBlackListController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsBlackListController.class);

    @Autowired
    private GoodsBlackListMapper goodsBlackListMapper;

    @RequestMapping(value = "/{goodsInfoId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@PathVariable Long goodsInfoId,
                                       @AuthenticationPrincipal User user) throws Exception {
        GoodsBlackListWithBLOBs goodsBlackList = goodsBlackListMapper.selectByGoodsId(goodsInfoId);
        if (goodsBlackList != null) {
            return ResponseEntity.ok().body(JSON.toJSONString(goodsBlackList.getJsonFormat()));
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody GoodsBlackListWithBLOBs goodsBlackList,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request save goods black list:{}", user.getId(), JSON.toJSONString(goodsBlackList));
        if (goodsBlackList.getId() == null) {
            goodsBlackList.setCreatedBy(user.getId().toString());
            goodsBlackList.setCreatedTime(new Date());
            goodsBlackListMapper.insert(goodsBlackList);
        } else {
            // TODO: check user have right to update goods
            goodsBlackList.setUpdatedBy(user.getId().toString());
            goodsBlackList.setUpdatedTime(new Date());
            goodsBlackListMapper.updateByPrimaryKeyWithBLOBs(goodsBlackList);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/delete/{blackListId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Long blackListId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request remove goods black list:{}", user.getId(), blackListId);
        goodsBlackListMapper.deleteByPrimaryKey(blackListId);
        return ResponseEntity.ok().build();
    }

}
