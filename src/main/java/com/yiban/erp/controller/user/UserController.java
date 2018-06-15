package com.yiban.erp.controller.user;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.Constant;
import com.yiban.erp.constant.IdentifierType;
import com.yiban.erp.constant.UserStatus;
import com.yiban.erp.dao.UserAuthMapper;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.dao.UserRouteMapper;
import com.yiban.erp.dto.SaveUserAccessReq;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserAuth;
import com.yiban.erp.entities.UserRoute;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.auth.TokenManager;
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

import java.util.ArrayList;
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
    @Autowired
    private TokenManager tokenManager;

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
        logger.info("user:{} do save update user info", doUser.getId());
        if (updateUser.getId() != null) {
            User newUser = updateUser(updateUser, doUser);
            return ResponseEntity.ok().body(JSON.toJSONString(newUser));
        }else {
            logger.info("user:{} do create a new user info.", doUser.getId());
            User newUser = createUser(updateUser, doUser);
            return ResponseEntity.ok().body(JSON.toJSONString(newUser));
        }
    }

    @Transactional
    protected User createUser(User baseUser, User doUser) throws Exception {
        if (StringUtils.isBlank(baseUser.getNickname()) || StringUtils.isBlank(baseUser.getMobile())) {
            throw new BizException(ErrorCode.USER_REGISTER_PARAMS);
        }
        //必输字段是否存在
        if (StringUtils.isBlank(baseUser.getCredential())) {
            logger.warn("password miss.");
            throw new BizException(ErrorCode.LOGIN_PASSWORD_MISSING);
        }
        setUserIDCard(baseUser); //验证和设置身份证号
        //验证用户名和手机号是否意见存在，如果存在，不能新建
        User validUser1 = userMapper.findUserByNickName(baseUser.getNickname());
        if (validUser1 != null) {
            throw new BizException(ErrorCode.USER_REGISTER_NICKNAME_EXIST);
        }
        User validUser2 = userMapper.findUserByMobile(baseUser.getMobile());
        if (validUser2 != null) {
            throw new BizException(ErrorCode.USER_MOBILE_EXIST);
        }

        //验证都通过后，创建用户信息
        baseUser.setCompanyId(doUser.getCompanyId());
        baseUser.setStatus(UserStatus.UN_ACTIVATE.getCode());
        baseUser.setSuperUser(false);
        baseUser.setCreatedTime(new Date());
        baseUser.setCreatedBy(doUser.getNickname());

        int count = userMapper.insert(baseUser);
        if (count > 0 && baseUser.getId() > 0) {
            //创建登录密码信息
            String encryptedPass = new BCryptPasswordEncoder().encode(baseUser.getCredential());

            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(baseUser.getId());
            userAuth.setIdentifierType(IdentifierType.USERNAME.name());
            userAuth.setIdentifier(baseUser.getNickname());
            userAuth.setCredential(encryptedPass);
            userAuth.setVerified(true);
            userAuth.setCreatedBy(doUser.getNickname());
            userAuth.setCreatedTime(new Date());
            int usernameResult = userAuthMapper.insert(userAuth);

            userAuth = new UserAuth();
            userAuth.setUserId(baseUser.getId());
            userAuth.setIdentifierType(IdentifierType.MOBILE.name());
            userAuth.setIdentifier(baseUser.getMobile());
            userAuth.setCredential(encryptedPass);
            userAuth.setVerified(true);
            userAuth.setCreatedBy(doUser.getNickname());
            userAuth.setCreatedTime(new Date());
            int mobileResult = userAuthMapper.insert(userAuth);

            if (usernameResult > 0 && mobileResult > 0) {
                return baseUser;
            }
        }
        throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
    }

    private void setUserIDCard(User setUser) throws BizException {
        String idCard = setUser.getIdcard();
        if (StringUtils.isNotBlank(idCard)) {
            idCard = idCard.toUpperCase(); //转换大写
            if (!IDCardUtil.isValid(idCard)) {
                logger.warn("update idCard is error. idCard:{}", idCard);
                throw new BizException(ErrorCode.USER_REGISTER_IDCARD_ERROR);
            }
            else {
                setUser.setIdcard(idCard);
                setUser.setBirthday(IDCardUtil.getBirthday(idCard));
                setUser.setSex(IDCardUtil.getSex(idCard));
            }
        }
    }

    @Transactional
    protected User updateUser(User updateUser, User doUser) throws Exception {
        logger.info("user:{} update user info.", doUser.getId());
        if (updateUser.getId() == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        setUserIDCard(updateUser);
        updateUser.setUpdatedTime(new Date());
        updateUser.setUpdatedBy(doUser.getNickname());

        int count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (count > 0) {
            User newUser = userMapper.getDetailById(updateUser.getId());
            return newUser;
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
            throw new BizException(ErrorCode.USER_REGISTER_NICKNAME_EXIST);
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
    @RequestMapping(value = "/update/nickname", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateNickName(@RequestBody Map requestMap,
                                                 @AuthenticationPrincipal User doUser) throws Exception {
        logger.info("user:{} request to do update nickname, params:{}", doUser.getId(), JSON.toJSONString(requestMap));
        Object userIdObj = requestMap.get("userId");
        String nickname = ((String) requestMap.getOrDefault("nickname", "")).trim();
        if (userIdObj == null || StringUtils.isEmpty(nickname.trim())) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        Long userId = Long.valueOf(String.valueOf(userIdObj));
        User validateUser = userMapper.findUserByNickName(nickname);
        if (validateUser != null) {
            throw new BizException(ErrorCode.USER_REGISTER_NICKNAME_EXIST);
        }
        User oldUser = userMapper.getDetailById(userId);
        if (oldUser == null || !oldUser.getCompanyId().equals(doUser.getCompanyId())) {
            logger.warn("get user fail or userId is error.");
            throw new BizException(ErrorCode.USER_GET_FAIL);
        }
        if (!UserStatus.NORMAL.getCode().equals(oldUser.getStatus())) {
            throw new BizException(ErrorCode.USER_LOGIN_UN_ACTIVATE);
        }
        List<UserAuth> userAuths = userAuthMapper.findByUserId(oldUser.getId());
        if (userAuths == null || userAuths.isEmpty()) {
            throw new BizException(ErrorCode.USER_MOBILE_PASSWORD_MISS);
        }
        String password = "";
        UserAuth userNameAuth = null;
        for (UserAuth auth : userAuths) {
            if (IdentifierType.USERNAME.name().equalsIgnoreCase(auth.getIdentifierType())) {
                userNameAuth = auth;
            }
            if (IdentifierType.MOBILE.name().equalsIgnoreCase(auth.getIdentifierType())) {
                password = auth.getCredential();
            }
        }

        //修改用户手机号和验证手机号号
        oldUser.setNickname(nickname);
        oldUser.setUpdatedBy(doUser.getNickname());
        oldUser.setUpdatedTime(new Date());
        int count = userMapper.updateNickName(oldUser);
        if (count > 0) {
            if (userNameAuth == null) {
                // insert user auths of USERNAME
                UserAuth userAuth = new UserAuth();
                userAuth.setUserId(oldUser.getId());
                userAuth.setIdentifierType(IdentifierType.USERNAME.name());
                userAuth.setIdentifier(oldUser.getNickname());
                userAuth.setCredential(password);
                userAuth.setVerified(true);
                userAuth.setCreatedBy(doUser.getNickname());
                userAuth.setCreatedTime(new Date());
                userAuthMapper.insert(userAuth);
            }else {
                //update user auths of USERNAME
                userAuthMapper.updateAuthIdentifier(oldUser.getId(), IdentifierType.USERNAME.name(), nickname, doUser.getNickname(), new Date());
            }
            return ResponseEntity.ok().build();
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
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
        User oldUser = userMapper.getDetailById(userId);
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
        User user = userMapper.getDetailById(userId);
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

    @Transactional
    @RequestMapping(value = "/save/access", method = RequestMethod.PUT, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveUserAccess(@RequestBody SaveUserAccessReq accessReq,
                                                 @AuthenticationPrincipal User doUser) throws Exception {
        logger.info("doUser:{} save user access info. accessReq:{}", doUser.getId(), JSON.toJSONString(accessReq));
        User updateUser = userMapper.getDetailById(accessReq.getUserId());
        if (updateUser == null || updateUser.getId() == null) {
            logger.warn("get update user info fail by userId:{}", accessReq.getUserId());
            throw new BizException(ErrorCode.USER_GET_FAIL);
        }
        if (!updateUser.getCompanyId().equals(doUser.getCompanyId())) {
            logger.error("do user:{} and update user:{} company is not equals.", doUser.getId(), updateUser.getId());
            throw new BizRuntimeException(ErrorCode.ACCESS_PERMISSION);
        }
        //先检查状态是否有变化，如果有，先修改状态的变化
        if (accessReq.getStatus() != null && !accessReq.getStatus().equals(updateUser.getStatus())) {
            logger.info("updateUser:{} status have change. old status:{} new status:{} do by user:{}",
                    updateUser.getId(), updateUser.getStatus(), accessReq.getStatus(), doUser.getId());
            //如果是离职状态的修改，需要把用户名和手机号进行数据格式化.
            if (accessReq.getStatus() < 0) {
                String pre = updateUser.getCompanyId() + "DEL-";
                updateUser.setNickname(pre + updateUser.getNickname());
                updateUser.setMobile(pre + updateUser.getMobile());
                updateUser.setStatus(UserStatus.DELETE.getCode());
                updateUser.setUpdatedTime(new Date());
                updateUser.setUpdatedBy(doUser.getNickname());
                int count = userMapper.updateUserStatusToDelete(updateUser);
                if (count > 0) {
                    // 删除user_auths表中同一个用户的所有记录
                    userAuthMapper.deleteByUserId(updateUser.getId());
                    //做到这步，离职数据修改成功，直接返回，不需要在修改菜单项数据
                    tokenManager.removeToken(updateUser);
                    return ResponseEntity.ok().build();
                }else {
                    throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
                }
            }else {
                updateUser.setStatus(accessReq.getStatus() > 0 ? UserStatus.NORMAL.getCode() : UserStatus.UN_ACTIVATE.getCode());
                updateUser.setUpdatedBy(doUser.getNickname());
                updateUser.setUpdatedTime(new Date());
                int count = userMapper.updateByPrimaryKeySelective(updateUser);
                if (count <= 0 ) {
                    throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
                }
            }
        }
        List<String> routes = accessReq.getRouteNames();
        if (routes == null) {
            return ResponseEntity.ok().build(); //如果权限列表是空值，直接算成功了返回，不修改权限数据.
        }
        List<UserRoute> oldRoute = userRouteMapper.getByUserId(updateUser.getId());
        List<String> oldRouteNames = new ArrayList<>(); //主要用户清除用户登录态的token使用
        oldRoute.stream().forEach(item -> oldRouteNames.add(item.getRouteName()));
        //修改用户的所有权限，直接删除原来的，然后添加所有新的数据，注意，超级管理员不能删除授权页面的数据
        userRouteMapper.deleteByUserId(updateUser.getId());
        List<UserRoute> newUserRoute = new ArrayList<>();
        if (updateUser.getSuperUser() != null && updateUser.getSuperUser()) {
            //超级用户，如果权限列表中没有授权页面的权限，需要加上，相当于超级用户至少存在一个授权页面。
            if (!routes.contains(Constant.ACCESS_PAGE)) {
                UserRoute route = new UserRoute();
                route.setUserId(updateUser.getId());
                route.setRouteName(Constant.ACCESS_PAGE);
                route.setCreateBy(doUser.getNickname());
                route.setCreateTime(new Date());
                newUserRoute.add(route);
            }
        }
        for (String routeName : routes) {
            UserRoute route = new UserRoute();
            route.setUserId(updateUser.getId());
            route.setRouteName(routeName);
            route.setCreateBy(doUser.getNickname());
            route.setCreateTime(new Date());
            newUserRoute.add(route);
        }
        if (!newUserRoute.isEmpty()) {
            userRouteMapper.insertBatch(newUserRoute);
            //剔除修改页面权限的用户登录态，让用户重新登录
            tokenManager.removeToken(updateUser);
        }
        return ResponseEntity.ok().build(); //返回成功
    }

}
