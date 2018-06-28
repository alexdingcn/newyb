package com.yiban.erp.controller.util;

import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.yiban.erp.dao.BillTradeLogMapper;
import com.yiban.erp.entities.BillTradeLog;
import com.yiban.erp.exception.*;
import com.yiban.erp.util.AESUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/util")
public class UtilController {
    private static final Logger logger = LoggerFactory.getLogger(UtilController.class);

    @Autowired
    private BillTradeLogMapper billTradeLogMapper;
    /**
     * 获取拼音缩写
     *
     * @return
     */
    @RequestMapping(value = "/pinyinAbbr", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPinyinAbbr(@RequestBody Map map) throws Exception {
        String name = (String) map.get("name");
            if (StringUtils.isEmpty(name)) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok().body(PinyinHelper.getShortPinyin(name).toUpperCase());
        } catch (PinyinException e) {
            logger.error("Failed to translate to pinyin, string={}", name);
            throw new BizRuntimeException(ErrorCode.FAILED_PINGYIN_EXCEPTION, e.toString());
        }
    }

    /**
     * 获取推送的Tradelog
     *
     * @return
     */
    @RequestMapping(value = "/tradelog", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> receiveTradeLog(@RequestBody String body) throws Exception {
        String result = AESUtil.decrypt(body);
        if (StringUtils.isNotEmpty(result)) {
            try {
                BillTradeLog billTradeLog = new BillTradeLog();
                billTradeLog.setBody(result);
                billTradeLog.setCreatedTime(new Date());
                billTradeLogMapper.insert(billTradeLog);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }

            JSONObject returnObj = new JSONObject();
            returnObj.put("responseMessage", "详情推送成功!");
            returnObj.put("responseCode", "000000");

            JSONObject resultObj = new JSONObject();
            resultObj.put("response", returnObj);
            return ResponseEntity.ok().body(resultObj.toString());
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public ResponseEntity<String> testException(@RequestParam("code") Integer code) throws Exception {
        logger.info("test exception handler code:", code);
        ErrorInfo testExtra = new ErrorInfo();
        testExtra.setCode(code);
        testExtra.setMessage("test exception handler");
        switch (code) {
            case 1:
                throw new BizException(ErrorCode.FAILED_DELETE_FROM_DB);
            case 2:
                throw new BizException(ErrorCode.CUSTOMER_DEL_PARAMS_EMPTY, testExtra);
            case 3:
                throw new BizRuntimeException(ErrorCode.FAILED_PINGYIN_EXCEPTION);
            case 4:
                throw new BizRuntimeException(ErrorCode.FAILED_PINGYIN_EXCEPTION, "PinyinException");
            case 5:
                throw new IllegalArgumentException("IllegalArgumentException");
            case 6:
                throw new RuntimeException("RuntimeException");
            case 7:
                throw new Exception("Exception");
            case 8:
                throw new PermissionException(ErrorCode.ACCESS_PERMISSION);
            default:
                return ResponseEntity.ok().body("code > 8 ok");
        }
    }

}
