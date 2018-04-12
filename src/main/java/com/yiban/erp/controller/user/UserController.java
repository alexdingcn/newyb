package com.yiban.erp.controller.user;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取可用用户的列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list() {
        List<User> userList = userMapper.selectAll();
        return ResponseEntity.ok().body(JSON.toJSONString(userList));
    }

    @RequestMapping(value = "/valid/nickname", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validNickName(@RequestParam("nickname") String nickName) throws Exception {
        if (StringUtils.isBlank(nickName)) {
            throw new BizException(ErrorCode.LOGIN_USERNAME_MISSING);
        }
        User user = userMapper.findUserByNickName(nickName.trim());
        if (user == null) {
            return ResponseEntity.ok().build();
        }else {
            throw new BizException(ErrorCode.USER_NICKNAME_EXIST);
        }
    }

}
