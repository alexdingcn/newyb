package com.yiban.erp.controller.user;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.IdentifierType;
import com.yiban.erp.constant.UserStatus;
import com.yiban.erp.dao.UserAuthMapper;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.dao.UserRouteMapper;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserAuth;
import com.yiban.erp.entities.UserRole;
import com.yiban.erp.entities.UserRoute;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.util.PhoneService;
import com.yiban.erp.util.IDCardUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PhoneService phoneService;
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Autowired
    private UserRouteMapper userRouteMapper;

    /**
     * 获取可用用户的列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user) {
        List<User> userList = userMapper.selectAll(user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(userList));
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(@RequestParam("userId") Long userId) {
        User user = userMapper.getDetailById(userId);
        if (user != null) {
            user = user.getCompactUser();
        }
        return ResponseEntity.ok().body(JSON.toJSONString(user));
    }

    @RequestMapping(value = "/save", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody User updateUser,
                                       @AuthenticationPrincipal User doUser) throws Exception {
        logger.info("user:{} update user info.", doUser.getId());
        if (updateUser.getId() == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        String idCard = updateUser.getIdcard();
        if (StringUtils.isNotBlank(idCard)) {
            idCard = idCard.toUpperCase(); //转换大写
            if (!IDCardUtil.isValid(idCard)) {
                logger.warn("update idCard is error. idCard:{}", idCard);
                throw new BizException(ErrorCode.USER_REGISTER_IDCARD_ERROR);
            }
            else {
                updateUser.setIdcard(idCard);
                updateUser.setBirthday(IDCardUtil.getBirthday(idCard));
                updateUser.setSex(IDCardUtil.getSex(idCard));
            }
        }
        updateUser.setUpdatedTime(new Date());
        updateUser.setUpdatedBy(doUser.getNickname());

        int count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (count > 0) {
            User newUser = userMapper.getDetailById(updateUser.getId());
            return ResponseEntity.ok().body(JSON.toJSONString(newUser));
        }else {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }

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

    @RequestMapping(value = "/valid/mobile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validMobile(@RequestParam("mobile") String mobile) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            throw new BizException(ErrorCode.USER_MOBILE_MISSING);
        }
        User user = userMapper.findUserByMobile(mobile);
        if (user == null) {
            return ResponseEntity.ok().build();
        }else {
            throw new BizException(ErrorCode.USER_MOBILE_EXIST);
        }
    }

    @Transactional
    @RequestMapping(value = "/update/mobile", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateMobile(@RequestBody Map requestMap,
                                               @AuthenticationPrincipal User doUser) throws Exception {
        Object userIdObj = requestMap.get("userId");
        if (userIdObj == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        Long userId = Long.valueOf(String.valueOf(userIdObj));
        String mobile = (String) requestMap.getOrDefault("mobile", "");
        String verifyCode = (String) requestMap.getOrDefault("verifyCode", "");

        logger.info("doUser:{} request update userId:{} mobile:{}", doUser.getId(), userId, mobile);
        if (userId == null || StringUtils.isBlank(mobile)) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        User existUser = userMapper.findUserByMobile(mobile);
        if (existUser != null) {
            throw new BizException(ErrorCode.USER_MOBILE_EXIST);
        }
        User oldUser = userMapper.selectByPrimaryKey(userId);
        if (oldUser == null) {
            throw new BizException(ErrorCode.USER_GET_FAIL);
        }
        if (!UserStatus.NORMAL.getCode().equals(oldUser.getStatus())) {
            logger.warn("user status is not normal. userId:{}", userId);
            throw new BizException(ErrorCode.USER_LOGIN_UN_ACTIVATE);
        }
        //验证短信验证码
        if (!phoneService.validVerifyCode(mobile, verifyCode)) {
            throw new BizException(ErrorCode.VERIFY_CODE_VALIDATE_FAIL);
        }

        //修改用户手机号和验证手机号号
        oldUser.setMobile(mobile);
        oldUser.setUpdatedBy(doUser.getNickname());
        oldUser.setUpdatedTime(new Date());
        int count = userMapper.updateMobile(oldUser);
        if (count > 0) {
            //update user auths of Mobile
            userAuthMapper.updateAuthIdentifier(oldUser.getId(), IdentifierType.MOBILE.name(), mobile, doUser.getNickname(), new Date());
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }

    @Transactional
    @RequestMapping(value = "/update/password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePassword(@RequestBody Map requestMap,
                                                 @AuthenticationPrincipal User doUser) throws Exception {
        logger.info("doUser request update password.", doUser.getId());
        Object userIdObj = requestMap.get("userId");
        if (userIdObj == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        Long userId = Long.valueOf(String.valueOf(userIdObj));
        String oldPassword = (String) requestMap.getOrDefault("oldPass", "");
        String newPassword = (String) requestMap.getOrDefault("newPass", "");

        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            logger.warn("update password but request params is null.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            logger.warn("get user result is null. userId:{}", userId);
            throw new BizException(ErrorCode.USER_GET_FAIL);
        }
        if (!UserStatus.NORMAL.getCode().equals(user.getStatus())) {
            logger.warn("user status is not normal. userId:{}", userId);
            throw new BizException(ErrorCode.USER_LOGIN_UN_ACTIVATE);
        }
        //先验证用户名下的密码是否正确，如果正确，才能修改
        UserAuth userAuth = userAuthMapper.findByIdentifier(user.getNickname(), IdentifierType.USERNAME.name());
        if (userAuth == null || !new BCryptPasswordEncoder().matches(oldPassword, userAuth.getCredential())) {
            throw new BizException(ErrorCode.LOGIN_PASSWORD_INVALID); //密码错误
        }
        // 验证通过，直接修改密码
        String encryptedPass = new BCryptPasswordEncoder().encode(newPassword);
        int count = userAuthMapper.updatePassword(user.getId(), encryptedPass, doUser.getNickname(), new Date());
        if (count > 0) {
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }

    @RequestMapping(value = "/route/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserRoutes(@RequestParam("userId") Long userId) {
        List<UserRoute> routes = userRouteMapper.getByUserId(userId);
        return ResponseEntity.ok().body(JSON.toJSONString(routes));
    }

}
