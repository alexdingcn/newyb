package com.yiban.erp.service.util;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.SystemConfigMapper;
import com.yiban.erp.entities.SystemConfig;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SystemConfigService {

    private static final String CACHE_KEY_PRE = "ERP.CONFIG.";

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigService.class);

    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public Map<String, SystemConfig> getConfigMap(User user) {
        //先从缓存获取，如果缓存中存在，直接返回，如果缓存中不存在，查询数据库
        if (user.getCompanyId() == null) {
            return new HashMap<>();
        }
        String cacheKey = CACHE_KEY_PRE + user.getCompanyId();
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String cacheValue = operations.get(cacheKey);
        if (StringUtils.isNotEmpty(cacheValue)) {
            //缓存存在时，进行直接组装返回结果
            List<SystemConfig> configs = JSON.parseArray(cacheValue, SystemConfig.class);
            return listToMap(configs);
        }
        //如果缓存获取不到是，从数据库查询
        List<SystemConfig> configList = systemConfigMapper.getAll(user.getCompanyId());
        if (!configList.isEmpty()) {
            //不为空的时候，存入缓存
            String cache = JSON.toJSONString(configList);
            operations.set(cacheKey, cache, 7, TimeUnit.DAYS); //缓存7天
        }
        return listToMap(configList);
    }

    private Map<String, SystemConfig> listToMap(List<SystemConfig> configs) {
        Map<String, SystemConfig> result = new HashMap<>();
        if (configs == null || configs.isEmpty()) {
            return result;
        }
        //以keyName为key,config为value
        configs.stream().forEach(item -> result.put(item.getKeyName(), item));
        return result;
    }

    private void refreshCache(Integer companyId) {
        String cacheKey = CACHE_KEY_PRE + companyId;
        List<SystemConfig> configs = systemConfigMapper.getAll(companyId);
        if (!configs.isEmpty()) {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            //不为空的时候，存入缓存
            String cache = JSON.toJSONString(configs);
            operations.set(cacheKey, cache, 7, TimeUnit.DAYS); //缓存7天
        }
    }

    @Transactional
    public void saveOne(SystemConfig config, User user) throws BizException {
        if (config == null || StringUtils.isEmpty(config.getKeyName())) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //如果id存在，验证下companyId
        if (config.getId() != null && !user.getCompanyId().equals(config.getCompanyId())) {
            throw new BizException(ErrorCode.ACCESS_PERMISSION);
        }
        //如果验证通过
        if (config.getId() != null) {
            //做update
            config.setUpdatedTime(new Date());
            config.setUpdatedBy(user.getNickname());
            systemConfigMapper.updateByPrimaryKeySelective(config);
        }else {
            config.setCompanyId(user.getCompanyId());
            config.setCreatedBy(user.getNickname());
            config.setCreatedTime(new Date());
            systemConfigMapper.insert(config);
        }
        //去更新下缓存
        refreshCache(user.getCompanyId());
    }
}
