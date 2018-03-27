package com.yiban.erp.controller.loan;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.controller.good.GoodController;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
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
        String bizNo = UUID.randomUUID().toString();
        postData.add("biz_no", bizNo);
        postData.add("return_url", "http://192.168.1.188:8082/#/loan/bizlicense");
        postData.add("notify_url", "https://www.yibanjf.com");
        postData.add("scene_id", SCENE_ID);
        postData.add("comparison_type", String.valueOf(1));
        postData.add("idcard_mode", String.valueOf(2));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(postData, headers);

        try {
            JSONObject result = restTemplate.postForEntity(faceIdTokenUrl, request, JSONObject.class).getBody();
            logger.info(result.toString());
            String jumpUrl = faceIdVerifyUrl + "?token=" + result.getString("token");
            return ResponseEntity.ok().body(jumpUrl);
        } catch (RestClientException ex) {
            logger.error("Failed to get faceId token, {}", ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ErrorCode.GET_FACEID_TOKEN_FAIL.toString());
    }


    @RequestMapping(value = "/face/result", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getFaceResult(@RequestBody Map requestMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(faceIdResultUrl)
                .queryParam("api_key", faceIdApiKey)
                .queryParam("api_secret", faceIdApiSecret)
                .queryParam("biz_id", (String) requestMap.getOrDefault("bizNo", ""));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            JSONObject result = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, JSONObject.class).getBody();
            if (result != null) {
                JSONObject verifyResult = result.getJSONObject("verify_result");
                if (verifyResult != null && "OK".equalsIgnoreCase(result.getString("status"))) {
                    JSONObject resultObj = new JSONObject();
                    resultObj.put("livenessResult", result.getJSONObject("liveness_result").getString("result"));
                    resultObj.put("idcard", result.getJSONObject("idcard_info"));
                    return ResponseEntity.ok().body(resultObj.toJSONString());
                }
            }
        } catch (RestClientException ex) {
            logger.error("Failed to get faceId result, {}", ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ErrorCode.GET_FACEID_RESULT_FAIL.toString());
    }

    @RequestMapping(value = "/bizlic/ocr", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getBizLicenseOcrResult(@RequestBody Map requestMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String host = "https://dm-58.data.aliyun.com";
        String path = "/rest/160601/ocr/ocr_business_license.json";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(faceIdResultUrl)
                .queryParam("api_key", faceIdApiKey)
                .queryParam("api_secret", faceIdApiSecret)
                .queryParam("biz_id", (String) requestMap.getOrDefault("bizNo", ""));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            JSONObject result = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, JSONObject.class).getBody();
            if (result != null) {
                JSONObject verifyResult = result.getJSONObject("verify_result");
                if (verifyResult != null && "OK".equalsIgnoreCase(result.getString("status"))) {
                    JSONObject resultObj = new JSONObject();
                    resultObj.put("livenessResult", result.getJSONObject("liveness_result").getString("result"));
                    resultObj.put("idcard", result.getJSONObject("idcard_info"));
                    return ResponseEntity.ok().body(resultObj.toJSONString());
                }
            }
        } catch (RestClientException ex) {
            logger.error("Failed to get faceId result, {}", ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ErrorCode.GET_FACEID_RESULT_FAIL.toString());
    }

}
