package com.yiban.erp.controller.util;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.OptionsType;
import com.yiban.erp.dao.OptionsMapper;
import com.yiban.erp.dto.OptionValue;
import com.yiban.erp.entities.Options;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
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

import java.util.*;

@RestController
@RequestMapping("/options")
public class OptionsController {
    private static final Logger logger = LoggerFactory.getLogger(OptionsController.class);

    @Autowired
    private OptionsMapper optionsMapper;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSON> getPinyinAbbr(@AuthenticationPrincipal User user,
                                                @RequestBody List<String> options) throws Exception {
        List<String> queryOptions = null;
        if (options != null) {
            queryOptions = new ArrayList<>();
            for (String option : options) {
                if (OptionsType.getByName(option.toUpperCase()) != null) {
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
        return ResponseEntity.ok().body((JSON) JSON.toJSON(result));
    }

    @RequestMapping(value = "/type", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllTypes() {
        List<OptionValue> options = new ArrayList<>();
        for (OptionsType type : OptionsType.values()) {
            OptionValue value = new OptionValue();
            value.setValue(type.name());
            value.setDesc(type.getDescription());
            options.add(value);
        }
        return ResponseEntity.ok().body(JSON.toJSONString(options));
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPinyinAbbr(@AuthenticationPrincipal User user,
                                                @RequestBody Options option) throws Exception {
        if (option == null || StringUtils.isEmpty(option.getValue())) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        int result = 0;
        if (option.getId() == null) {
            option.setCreatedBy(user.getNickname());
            option.setCompanyId(user.getCompanyId());
            option.setCreatedTime(new Date());
            option.setEnabled(true);
            result = optionsMapper.insert(option);
        } else {
            option.setUpdatedBy(user.getNickname());
            option.setUpdatedTime(new Date());
            result = optionsMapper.updateByPrimaryKey(option);
        }
        if (result <= 0) {
            throw new BizException(ErrorCode.FAILED_INSERT_OR_UPDATE_FROM_DB);
        }
        return ResponseEntity.ok().build();
    }


}
