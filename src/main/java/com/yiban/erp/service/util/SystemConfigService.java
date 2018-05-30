package com.yiban.erp.service.util;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.ConfigKey;
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

    /**
     * 判断某一步订单流程是否存在
     * 注意，改方法只做审批流程的判断
     * @param companyId
     * @param key
     * @return 默认流程未开启,
     */
    public boolean haveOrderFlow(Integer companyId, ConfigKey key) {
        if (companyId ==null || key == null) {
            return false;
        }
        SystemConfig config = getByKeyName(companyId, key.name());
        if (config == null) {
            return false;
        }
        switch (key) {
            case BUY_CHECK:
            case BUY_QUALITY_CHECK:
            case BUY_FINAL_CHECK:
            case BUY_BACK_BM_CHECK:
            case BUY_BACK_QA_CHECK:
            case BUY_BACK_QUALITY_CHECK:
            case SALE_CHECK:
            case SALE_BACK_SM_CHECK:
            case SALE_BACK_QA_CHECK:
            case SALE_BACK_QUALITY_CHECK:
            case SALE_BACK_FINAL_CHECK:
                return "open".equalsIgnoreCase(config.getKeyValue()); //不是打开状态的情况下直接返回未开启流程
        }
        return false;
    }

    public SystemConfig getByKeyName(Integer companyId, String keyName) {
        Map<String, SystemConfig> map = getConfigMap(companyId);
        return map.get(keyName);
    }


    public Map<String, SystemConfig> getConfigMap(Integer companyId) {
        //先从缓存获取，如果缓存中存在，直接返回，如果缓存中不存在，查询数据库
        if (companyId == null) {
            return new HashMap<>();
        }
        String cacheKey = CACHE_KEY_PRE + companyId;
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String cacheValue = operations.get(cacheKey);
        if (StringUtils.isNotEmpty(cacheValue)) {
            //缓存存在时，进行直接组装返回结果
            List<SystemConfig> configs = JSON.parseArray(cacheValue, SystemConfig.class);
            return listToMap(configs);
        }
        //如果缓存获取不到是，从数据库查询
        List<SystemConfig> configList = systemConfigMapper.getAll(companyId);
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
    public void saveOne(SystemConfig config, User user, boolean cacheRefresh) throws BizException {
        if (config == null || StringUtils.isEmpty(config.getKeyName())) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //如果id存在，验证下companyId
        if (config.getId() != null && !user.getCompanyId().equals(config.getCompanyId())) {
            throw new BizException(ErrorCode.ACCESS_PERMISSION);
        }
        ConfigKey key = ConfigKey.getByName(config.getKeyName());
        if (key == null) {
            logger.warn("system config key name is error.");
            throw new BizException(ErrorCode.PARAMETER_MISSING);
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
        if (cacheRefresh) {
            //去更新下缓存
            refreshCache(user.getCompanyId());
        }
    }


    public void saveList(List<SystemConfig> configs, User user) throws BizException {
        if (configs == null || configs.isEmpty()) {
            logger.info("config list is empty.");
            return;
        }
        //直接拆分循环一个保存
        for (SystemConfig config : configs) {
            saveOne(config, user, false);
        }

        //循环完成后，直接一次调用更新缓存数据
        refreshCache(user.getCompanyId());
    }

}
