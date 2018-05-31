package com.yiban.erpcustomer.controller.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

@RestController
@RequestMapping("/wx")
public class WxCallbackController {
    private static final Logger logger = LoggerFactory.getLogger(WxCallbackController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.app.id}")
    private String wxAppId;
    @Value("${wx.app.secret}")
    private String wxAppSecret;

    @RequestMapping(value = "/callback", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<JSON> getCallback(HttpServletRequest request) {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        logger.info("Receive wx callback, code={}, state={}", code, state);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", wxAppId, wxAppSecret, code);

        try {
            ResponseEntity<JSONObject> resp = restTemplate.getForEntity(url, JSONObject.class);
            if (resp != null) {
                if (resp.getStatusCode() == HttpStatus.OK) {
                    JSONObject respBody = resp.getBody();
                    /*
                    {   "access_token":"ACCESS_TOKEN",
                        "expires_in":7200,
                        "refresh_token":"REFRESH_TOKEN",
                        "openid":"OPENID",
                        "scope":"SCOPE" }
                     */
                    logger.info("response body:" + respBody.toString());


                    logger.info("User info:" + getUserInfo(respBody.getString("access_token"), respBody.getString("openid")));

                }
            }

            return ResponseEntity.ok().build();
        } catch (RestClientException ex) {
            logger.error("Failed to get faceId token, {}", ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    private String getUserInfo(String accessToken, String openId) {
        String url = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN", accessToken, openId);

        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (resp != null) {
                if (resp.getStatusCode() == HttpStatus.OK) {
                    String respBody = resp.getBody();
                    logger.info("response body:" + respBody);
                    return respBody;
                }
            }
        } catch (RestClientException ex) {
            logger.error("Failed to get faceId token, {}", ex.getMessage());
        }
        return null;
    }
}
