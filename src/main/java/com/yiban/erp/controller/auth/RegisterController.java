package com.yiban.erp.controller.auth;


import com.yiban.erp.constant.Constant;
import com.yiban.erp.constant.IdentifierType;
import com.yiban.erp.constant.UserStatus;
import com.yiban.erp.dao.CompanyMapper;
import com.yiban.erp.dao.UserAuthMapper;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.dao.UserRouteMapper;
import com.yiban.erp.entities.Company;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserAuth;
import com.yiban.erp.entities.UserRoute;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.util.PhoneService;
import com.yiban.erp.util.IDCardUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Autowired
    private UserRouteMapper userRouteMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private PhoneService phoneService;

    @Value("${newCompany.expiredDays}")
    private int expiredDays;

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody Map requestMap) throws Exception {
        String username = (String) requestMap.getOrDefault("userName", "");
        String password = (String) requestMap.getOrDefault("password", "");
        String mobile = (String) requestMap.getOrDefault("mobile", "");
        String companyName = (String) requestMap.getOrDefault("company", "");
        String businessLicense = (String) requestMap.getOrDefault("businessLicense", "");
        String realName = (String) requestMap.getOrDefault("realName", "");
        String idCard = (String) requestMap.getOrDefault("idCard", "");
        String verifyCode = (String) requestMap.getOrDefault("verifyCode", "");

        if (StringUtils.isBlank(companyName)) {
            logger.warn("required params is null.");
            throw new BizException(ErrorCode.USER_REGISTER_PARAMS);
        }
        if (StringUtils.isBlank(password) || StringUtils.isBlank(mobile) || StringUtils.isBlank(verifyCode)) {
            logger.warn("required params is null.");
            throw new BizException(ErrorCode.USER_REGISTER_PARAMS);
        }

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(idCard)) {
            username = username.trim();
            idCard = idCard.toUpperCase(); //字母转大写
            //验证身份证号是否正确
            if (!IDCardUtil.isValid(idCard)) {
                logger.warn("register IDCard is error.");
                throw new BizException(ErrorCode.USER_REGISTER_IDCARD_ERROR);
            }
        }

        if (!phoneService.validVerifyCode(mobile, verifyCode)) {
            //短信验证码错误
            throw new BizException(ErrorCode.VERIFY_CODE_VALIDATE_FAIL);
        }

        //验证用户名是否意见存在，如果存在了，提示错误
        User oldUser = userMapper.findUserByMobile(mobile);
        if (oldUser != null) {
            logger.warn("user mobile is existed. mobile:{}", mobile);
            throw new BizException(ErrorCode.USER_MOBILE_EXIST);
        }

        //验证公司的营业执照是否存在
        if (StringUtils.isNotBlank(businessLicense)) {
            Company oldCompany = companyMapper.getByLicense(businessLicense);
            if (oldCompany != null) {
                if (oldCompany.getEnabled() == null || !oldCompany.getEnabled()) {
                    logger.warn("company license is register but enabled is false. companyId:{}", oldCompany.getId());
                    throw new BizException(ErrorCode.USER_REGISTER_COMPANY_ENABLED);
                }else {
                    logger.warn("company is exist. companyId:{}", oldCompany.getId());
                    throw new BizException(ErrorCode.USER_REGISTER_COMPANY_EXIST);
                }
            }
        }

        //如果验证通过，先创建公司信息
        Company company = new Company();
        company.setName(companyName);
        company.setLicense(businessLicense);
        company.setEnabled(true);
        company.setExpiredTime(DateUtils.addDays(new Date(), expiredDays));
        company.setCreatedBy(mobile);
        company.setCreatedTime(new Date());
        int result = companyMapper.insert(company);
        if (result <= 0 || company.getId() == null || company.getId() <= 0) {
            logger.warn("company created fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }

        User user = new User();
        user.setCompanyId(company.getId());
        user.setNickname(username);
        user.setRealname(realName);
        user.setMobile(mobile);
        user.setCreatedBy(mobile);
        if (StringUtils.isNotBlank(idCard)) {
            user.setIdcard(idCard);
            user.setBirthday(IDCardUtil.getBirthday(idCard));
            user.setSex(IDCardUtil.getSex(idCard));
        }

        user.setStatus(UserStatus.NORMAL.getCode());
        user.setSuperUser(true); //超级管理员
        user.setCreatedTime(new Date());
        if (userMapper.insert(user) > 0) {
            String encryptedPass = new BCryptPasswordEncoder().encode(password);

            UserAuth userAuth;
            int usernameResult = 0;
            if (StringUtils.isNotBlank(username)) {
                userAuth = new UserAuth();
                userAuth.setUserId(user.getId());
                userAuth.setIdentifierType(IdentifierType.USERNAME.name());
                userAuth.setIdentifier(username);
                userAuth.setCredential(encryptedPass);
                userAuth.setVerified(true);
                userAuth.setCreatedBy(String.valueOf(user.getId()));
                userAuth.setCreatedTime(new Date());
                usernameResult = userAuthMapper.insert(userAuth);
            }

            userAuth = new UserAuth();
            userAuth.setUserId(user.getId());
            userAuth.setIdentifierType(IdentifierType.MOBILE.name());
            userAuth.setIdentifier(mobile);
            userAuth.setCredential(encryptedPass);
            userAuth.setVerified(true);
            userAuth.setCreatedBy(String.valueOf(user.getId()));
            userAuth.setCreatedTime(new Date());
            int mobileResult = userAuthMapper.insert(userAuth);

            //为超级管理员加入第一个权限功能页面
            UserRoute userRoute = new UserRoute();
            userRoute.setUserId(user.getId());
            userRoute.setRouteName(Constant.ACCESS_PAGE);
            userRoute.setCreateBy(String.valueOf(user.getId()));
            userRoute.setCreateTime(new Date());
            int routeCount = userRouteMapper.insert(userRoute);

            if ((usernameResult > 0 || mobileResult > 0) && routeCount > 0) {
                return ResponseEntity.ok().build();
            }
        }
        logger.error("Failed to create user");
        throw new BizRuntimeException(ErrorCode.USER_REGISTER_EXCEPTION);
    }

}
