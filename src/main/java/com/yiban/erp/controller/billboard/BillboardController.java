package com.yiban.erp.controller.billboard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.entities.Billboard;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.billboard.BillboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;

@RestController
@RequestMapping(value = "/billboard")
public class BillboardController {
    private static final Logger logger = LoggerFactory.getLogger(BillboardController.class);

    @Autowired
    private BillboardService billboardService;

    @RequestMapping(value = "/getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getList(@AuthenticationPrincipal User user){
        Integer companyId = user.getCompanyId();
        List<Billboard> billboards = billboardService.getList(companyId);
        JSONObject result = new JSONObject();
        result.put("data",billboards);
    return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insert(@AuthenticationPrincipal User user,
                                         @RequestBody Billboard billboard){
        String userName = user.getNickname();
        Integer companyId = user.getCompanyId();
        billboard.setCreateBy(userName);
        billboard.setCompanyId(companyId);
        int result = billboardService.insert(billboard);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_INSERT_FROM_DB.toString());
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@AuthenticationPrincipal User user,
                                         @RequestBody Billboard billboard){
        String userName = user.getNickname();
        billboard.setCreateBy(userName);
        int result = billboardService.update(billboard);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_UPDATE_FROM_DB.toString());

    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@PathVariable(value = "id") int id){
        int result = billboardService.delete(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_DELETE_FROM_DB.toString());
    }

    @RequestMapping(value = "/sort", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sort(@RequestParam (value = "Id") int id,
                                       @RequestParam (value = "Number") int number){
        int result = billboardService.sort(id,number);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_DELETE_FROM_DB.toString());
    }
    @RequestMapping(value = "/display", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> display(@AuthenticationPrincipal User user){
        Integer companyId = user.getCompanyId();
        List<Billboard> billboards = billboardService.display(companyId);
        JSONObject result = new JSONObject();
        result.put("data",billboards);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }
}
