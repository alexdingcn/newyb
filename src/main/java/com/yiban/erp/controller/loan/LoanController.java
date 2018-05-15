package com.yiban.erp.controller.loan;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.controller.good.GoodsController;
import com.yiban.erp.dao.BizWxApplyMapper;
import com.yiban.erp.entities.BizWxApply;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/loan")
public class LoanController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

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

    @Value("${biz.ocr.url}")
    private String bizOcrUrl;
    @Value("${biz.ocr.appcode}")
    private String bizOcrAppCode;


    @Autowired
    private BizWxApplyMapper bizWxApplyMapper;

    @RequestMapping(value = "/face/token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> getFaceIdToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> postData = new LinkedMultiValueMap<>();
        postData.add("api_key", faceIdApiKey);
        postData.add("api_secret", faceIdApiSecret);
        String bizNo = UUID.randomUUID().toString();
        postData.add("biz_no", bizNo);
        postData.add("return_url", "https://wx.yibanmed.com/static/faceresult.html");
        postData.add("notify_url", "https://www.yibanjf.com");
        postData.add("scene_id", SCENE_ID);
        postData.add("comparison_type", String.valueOf(1));
        postData.add("idcard_mode", String.valueOf(2));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(postData, headers);

        try {
            JSONObject result = restTemplate.postForEntity(faceIdTokenUrl, request, JSONObject.class).getBody();
            logger.info(result.toString());
            String jumpUrl = faceIdVerifyUrl + "?token=" + result.getString("token");
            JSONObject resultObj = new JSONObject();
            resultObj.put("url", jumpUrl);
            resultObj.put("bizNo", result.getString("biz_id"));
            return ResponseEntity.ok().body(resultObj.toJSONString());
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
        logger.info("bizNo:" + requestMap.get("bizNo"));

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
    private ResponseEntity<String> getBizLicenseOcrResult(HttpServletRequest request) throws BizException {
        MultipartHttpServletRequest mtRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = mtRequest.getFiles("imagefile");
        if (files == null || files.isEmpty()) {
            throw new BizException(ErrorCode.FILE_UPLOAD_PARAMS_ERROR);
        }
        MultipartFile file = files.get(0);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "APPCODE " + bizOcrAppCode);

        String body = "";
        try {
            String imgData = Base64Utils.encodeToString(file.getBytes());
            body = "{\"image\":\"" + imgData + "\"}";
        } catch (IOException e) {
            logger.error("Failed to convert to base64, {}", e.getMessage());
            throw new BizException(ErrorCode.FILE_UPLOAD_PARAMS_ERROR);
        }

        HttpEntity<String> postData = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> result = restTemplate.postForEntity(bizOcrUrl, postData, String.class);
            if (result != null) {
                return ResponseEntity.ok().body(result.getBody());
            }
        } catch (RestClientException ex) {
            logger.error("Failed to get ocr result, {}", ex.getMessage());
        }
        return ResponseEntity.badRequest().body(ErrorCode.GET_BIZOCR_RESULT_FAIL.toString());
    }


    @RequestMapping(value = "/wx/apply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> saveWxLoanApply(@RequestBody BizWxApply bizApply) throws BizException {
        if (bizApply != null) {
            bizApply.setCreatedBy("weixin");
            bizApply.setCreatedTime(new Date());

            int result = bizWxApplyMapper.insert(bizApply);
            if (result > 0) {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().body(ErrorCode.FAILED_INSERT_FROM_DB.toString());
    }
}
