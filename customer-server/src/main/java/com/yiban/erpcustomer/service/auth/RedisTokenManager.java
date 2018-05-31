package com.yiban.erpcustomer.service.auth;

import com.alibaba.fastjson.JSON;
import com.yiban.erpcustomer.entities.User;
import com.yiban.erpcustomer.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenManager implements TokenManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisTokenManager.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String createToken(User user, List<String> accessApiList) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException(ErrorCode.USER_GET_FAIL.getMessage());
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //token = companyId.userId.uuid;
        StringBuilder cacheKey = new StringBuilder();
        cacheKey.append(user.getCompanyId());
        cacheKey.append(".");
        cacheKey.append(user.getId());

        String token = cacheKey.toString() + "." + uuid;

        TokenModel tokenModel = new TokenModel();
        tokenModel.setUserId(user.getId());
        tokenModel.setUser(user);
        tokenModel.setAccessApiList(accessApiList);
        tokenModel.setToken(token);
        Calendar calendar = Calendar.getInstance();
        calendar.add(EXPIRATIONTIME, Calendar.DATE);
        tokenModel.setExpiredTime(calendar.getTime());

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String cacheValue = JSON.toJSONString(tokenModel);
        operations.set(cacheKey.toString(), cacheValue, EXPIRATIONTIME, TimeUnit.DAYS);

        return token;
    }


    @Override
    public TokenModel getTokenModel(String authentication) {
        if (StringUtils.isBlank(authentication)) {
            return null;
        }
        String token = authentication.replace(TOKEN_PREFIX, "");
        try {
            String cacheKey = token.substring(0, token.lastIndexOf("."));
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String cacheValue = operations.get(cacheKey);
            if (StringUtils.isBlank(cacheValue)) {
                return null;
            }
            TokenModel tokenModel = JSON.parseObject(cacheValue, TokenModel.class);
            if (tokenModel == null || !token.equals(tokenModel.getToken())) {
                return null;
            }
            return tokenModel;
        }catch (Exception e) {
            logger.error("get token have an exception. ", e);
            return null;
        }
    }

    @Override
    public void removeToken(User user) {
        if (user == null) {
            return;
        }
        StringBuilder cacheKey = new StringBuilder();
        cacheKey.append(user.getCompanyId());
        cacheKey.append(".");
        cacheKey.append(user.getId());
        stringRedisTemplate.delete(cacheKey.toString());
    }
}
