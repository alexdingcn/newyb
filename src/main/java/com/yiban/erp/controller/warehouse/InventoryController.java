package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yiban.erp.dto.InventorySearch;
import com.yiban.erp.entities.RepertoryInventory;
import com.yiban.erp.entities.RepertoryInventoryDetail;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.warehouse.InventoryService;
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
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);


    @Autowired
    private InventoryService inventoryService;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderList(@RequestBody InventorySearch search,
                                            @AuthenticationPrincipal User user) throws Exception {
        search.setCompanyId(user.getCompanyId());
        List<RepertoryInventory> inventories = new ArrayList<>();
        int totalCount = inventoryService.getOrderListCount(search);
        if (totalCount > 0) {
            inventories = inventoryService.getOrderList(search);
        }
        JSONObject result = new JSONObject();
        result.put("totalCount", totalCount);
        result.put("data", inventories);
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/{inventoryId}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDetails(@PathVariable Long inventoryId) {
        logger.debug("request inventory details by inventoryId:{}", inventoryId);
        List<RepertoryInventoryDetail> details = inventoryService.getDetails(inventoryId);
        return ResponseEntity.ok().body(JSON.toJSONString(details, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/cancel/{inventoryId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cancel(@PathVariable Long inventoryId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request to cancel inventory by id:{}", user.getId(), inventoryId);
        inventoryService.cancelOrder(inventoryId, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody RepertoryInventory inventory,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save inventory order params:{}", user.getId(), JSON.toJSONString(inventory));
        inventoryService.saveInventory(inventory, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/view/{inventoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> inventoryView(@PathVariable Long inventoryId,
                                                @AuthenticationPrincipal User user) throws Exception {
        logger.debug("user:{} get inventory view by id:{}", user.getId(), inventoryId);
        RepertoryInventory inventory = inventoryService.getView(inventoryId, user);
        return ResponseEntity.ok().body(JSON.toJSONString(inventory, SerializerFeature.DisableCircularReferenceDetect));
    }

    @RequestMapping(value = "/check/{inventoryId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> inventoryCheck(@PathVariable Long inventoryId,
                                                 @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} check inventory by id:{}", user.getId(), inventoryId);
        inventoryService.check(inventoryId, user);
        return ResponseEntity.ok().build();
    }

}
