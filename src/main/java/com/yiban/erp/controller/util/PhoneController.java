package com.yiban.erp.controller.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yiban.erp.entities.VerifyCode;
import com.yiban.erp.dto.VerifyCodeReq;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/phone")
public class PhoneController {

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    @Value("${sms.accessKey}")
    private String accessKeyId;
    @Value("${sms.accessSecret}")
    private String accessKeySecret;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/verifycode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPhoneVerifyCode(@RequestBody VerifyCodeReq verifyCodeReq) throws Exception {
        if (StringUtils.isEmpty(verifyCodeReq.getMobile())) {
            throw new BizException(ErrorCode.MOBILE_MISSING);
        }

        // 验证码频繁校验
        String cacheKey = "MOBILE_" + verifyCodeReq.getMobile();
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        VerifyCode verifyCodeObj = JSONObject.parseObject(operations.get(cacheKey), VerifyCode.class);
        if (verifyCodeObj != null) {
            if (verifyCodeObj.getCount() > 3) {
                throw new BizException(ErrorCode.MOBILE_VERIFY_CODE_TOO_FREQUENT);
            }
            verifyCodeObj.setCount(verifyCodeObj.getCount() + 1);
        } else {
            verifyCodeObj = new VerifyCode();
            verifyCodeObj.setExpiredTime(DateUtils.addMinutes(new Date(), 10));
            verifyCodeObj.setCount(1);
        }

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(verifyCodeReq.getMobile());
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("医伴金服");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_123672350");

        // 放入缓存
        String verifyCode = RandomStringUtils.randomNumeric(4);
        request.setTemplateParam("{\"code\":\"" + verifyCode + "\"}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(UUID.randomUUID().toString());

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if (sendSmsResponse != null && "OK".equalsIgnoreCase(sendSmsResponse.getCode())) {
            verifyCodeObj.setMobile(verifyCodeReq.getMobile());
            verifyCodeObj.setCode(verifyCode);
            operations.set(cacheKey, JSON.toJSONString(verifyCodeObj), 10, TimeUnit.MINUTES);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(ErrorCode.SEND_VERIFY_CODE_FAIL.toString());
    }

}
