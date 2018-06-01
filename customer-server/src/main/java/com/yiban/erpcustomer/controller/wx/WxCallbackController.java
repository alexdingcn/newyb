package com.yiban.erpcustomer.controller.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erpcustomer.constant.IdentifierType;
import com.yiban.erpcustomer.constant.UserStatus;
import com.yiban.erpcustomer.dao.UserAuthMapper;
import com.yiban.erpcustomer.dao.UserMapper;
import com.yiban.erpcustomer.entities.User;
import com.yiban.erpcustomer.entities.UserAuth;
import com.yiban.erpcustomer.entities.WxUserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/wx")
public class WxCallbackController {
    private static final Logger logger = LoggerFactory.getLogger(WxCallbackController.class);

    private static final long EXPIRATION_TIME = 30 * 24 * 60 * 60000;     // 30天
    private static final String SECRET = "yiban#2018";            // JWT密码

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.app.id}")
    private String wxAppId;
    @Value("${wx.app.secret}")
    private String wxAppSecret;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAuthMapper userAuthMapper;

    @RequestMapping(value = "/callback", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<JSON> getCallback(HttpServletRequest request) {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        logger.info("Receive wx callback, code={}, state={}", code, state);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", wxAppId, wxAppSecret, code);

        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (resp != null) {
                if (resp.getStatusCode() == HttpStatus.OK) {
                    String respBody = resp.getBody();
                    /*
                    {   "access_token":"ACCESS_TOKEN",
                        "expires_in":7200,
                        "refresh_token":"REFRESH_TOKEN",
                        "openid":"OPENID",
                        "scope":"SCOPE" }
                     */
                    JSONObject obj = JSON.parseObject(respBody);
                    logger.info("response body:" + respBody.toString());
                    WxUserInfo wxUserInfo = getUserInfo(obj.getString("access_token"), obj.getString("openid"));

                    if (wxUserInfo != null) {
                        // register if not registered
                        UserAuth userAuth = userAuthMapper.findByIdentifier(wxUserInfo.getOpenid(), IdentifierType.WEIXIN.name());
                        User user = null;
                        if (userAuth == null) {
                            user = register(wxUserInfo);
                        } else {
                            user = userMapper.selectByPrimaryKey(userAuth.getId());
                        }
                        if (user != null) {
                            // generate cookie
                            return ResponseEntity.ok().body(generateJwtToken(user));
                        }
                    }
                }
            }

            return ResponseEntity.ok().build();
        } catch (RestClientException ex) {
            logger.error("Failed to get wx access_token, {}", ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    private JSONObject generateJwtToken(User user) {
        // 生成JWT
        String JWT = Jwts.builder()
                // 保存权限（角色）
                .claim("authorities", "")
                // 用户名写入标题
//                .setSubject(authentication.getName())
                .claim("user", JSON.toJSONString(user))
//                .setSubject(authentication.getName())
                // 有效期设置
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // 签名设置
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        // 生成登录日志

        JSONObject result = new JSONObject();
        result.put("jwt", JWT);
        result.put("userDetail", user);

        return result;
    }

    private User register(WxUserInfo wxUserInfo) {
        if (wxUserInfo != null) {
            User user = new User();
            user.setCompanyId(0);
            user.setType("c");
            user.setNickname(wxUserInfo.getNickname());
            user.setMobile("");
            user.setCreatedBy("weixin");
            user.setSex(wxUserInfo.getSex());
            user.setAvatarUrl(wxUserInfo.getHeadimgurl());

            user.setStatus(UserStatus.NORMAL.getCode());
            user.setSuperUser(true); //超级管理员
            user.setCreatedTime(new Date());
            if (userMapper.insert(user) > 0) {
                UserAuth userAuth = new UserAuth();
                userAuth.setUserId(user.getId());
                userAuth.setIdentifierType(IdentifierType.WEIXIN.name());
                userAuth.setIdentifier(wxUserInfo.getOpenid());
                userAuth.setVerified(true);
                userAuth.setCreatedBy(String.valueOf(user.getId()));
                userAuth.setCreatedTime(new Date());
                int result = userAuthMapper.insert(userAuth);
                if (result > 0) {
                    return user;
                }
            }
        }

        return null;
    }


    private WxUserInfo getUserInfo(String accessToken, String openId) {
        String url = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN", accessToken, openId);

        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (resp != null) {
                if (resp.getStatusCode() == HttpStatus.OK) {
                    String respBody = resp.getBody();
                    if (StringUtils.isNotEmpty(respBody)) {
                        logger.info("response body:" + respBody);
                        WxUserInfo wxUserInfo = JSON.parseObject(respBody, WxUserInfo.class);
                        return wxUserInfo;
                    }
                }
            }
        } catch (RestClientException ex) {
            logger.error("Failed to parse wx user info, {}", ex.getMessage());
        }
        return null;
    }
}
