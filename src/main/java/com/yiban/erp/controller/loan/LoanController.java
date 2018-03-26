package com.yiban.erp.controller.loan;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.controller.good.GoodController;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/loan")
public class LoanController {
    private static final Logger logger = LoggerFactory.getLogger(GoodController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${faceid.token.url}")
    private String faceIdTokenUrl;
    @Value("${faceid.verify.url}")
    private String faceIdVerifyUrl;
    @Value("${faceid.result.url}")
    private String faceIdResultUrl;
    @Value("${faceid.apiKey}")
    private String faceIdApiKey;
    @Value("${faceid.apiSecret}")
    private String faceIdApiSecret;

    private static final String SCENE_ID = "ybidcard";


    @RequestMapping(value = "/face/token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getFaceIdToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> postData = new LinkedMultiValueMap<>();
        postData.add("api_key", faceIdApiKey);
        postData.add("api_secret", faceIdApiSecret);
        postData.add("biz_no", UUID.randomUUID().toString());
        postData.add("return_url", "https://www.yibanmed.com");
        postData.add("notify_url", "https://www.yibanjf.com");
        postData.add("scene_id", SCENE_ID);
        postData.add("comparison_type", String.valueOf(1));
        postData.add("idcard_mode", String.valueOf(2));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(postData, headers);

        try {
            JSONObject result = restTemplate.postForEntity(faceIdTokenUrl, request, JSONObject.class).getBody();
            String jumpUrl = faceIdVerifyUrl + "?token=" + result.getString("token");
            return ResponseEntity.ok().body(jumpUrl);
        } catch (RestClientException ex) {
            logger.error("Failed to get faceId token, {}", ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ErrorCode.GET_FACEID_TOKEN_FAIL.toString());
    }

}
