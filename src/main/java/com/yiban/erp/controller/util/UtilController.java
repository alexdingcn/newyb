package com.yiban.erp.controller.util;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.yiban.erp.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/util")
public class UtilController {
    private static final Logger logger = LoggerFactory.getLogger(UtilController.class);

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
