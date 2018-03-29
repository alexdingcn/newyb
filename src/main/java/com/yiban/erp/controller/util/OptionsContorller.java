package com.yiban.erp.controller.util;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.OptionsType;
import com.yiban.erp.dao.OptionsMapper;
import com.yiban.erp.entities.Options;
import com.yiban.erp.entities.User;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/options")
public class OptionsContorller {
    private static final Logger logger = LoggerFactory.getLogger(OptionsContorller.class);

    @Autowired
    private OptionsMapper optionsMapper;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPinyinAbbr(@AuthenticationPrincipal User user,
                                                @RequestBody List<String> options) throws Exception {
        List<String> queryOptions = null;
        if (options != null) {
            queryOptions = new ArrayList<>();
            for (String option : options) {
                if (OptionsType.valueOf(option.toUpperCase()) != null) {
                    queryOptions.add(option.toUpperCase());
                }
            }
        }
        List<Options> optionsList = optionsMapper.findByTypes(user.getCompanyId(), queryOptions);

        Map<String, List<Options>> result = new HashMap<>();
        for (Options option : optionsList) {
            if (!result.containsKey(option.getType())) {
                result.put(option.getType(), new ArrayList<>());
            }
            result.get(option.getType()).add(option);
        }
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }


}
