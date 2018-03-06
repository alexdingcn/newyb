package com.yiban.erp.controller.util;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> getPinyinAbbr(@RequestBody Map map) {
        String name = (String) map.get("name");
            if (StringUtils.isEmpty(name)) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok().body(PinyinHelper.getShortPinyin(name).toUpperCase());
        } catch (PinyinException e) {
            logger.error("Failed to translate to pinyin, string={}", name);
            return ResponseEntity.badRequest().build();
        }
    }
}
