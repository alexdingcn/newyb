package com.yiban.erp.exception;

import com.yiban.erp.dao.SystemErrorMapper;
import com.yiban.erp.entities.SystemError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ErrorMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorMessageService.class);

    private Map<String, SystemError> cacheMap;

    @Autowired
    private SystemErrorMapper systemErrorMapper;

    @PostConstruct
    protected void init() {
        updateCacheMap();
    }

    public void updateCacheMap() {
        List<SystemError> errors = systemErrorMapper.getAll();
        if (errors.isEmpty()) {
            return;
        }
        for (SystemError error : errors) {
            if (error.getKey() != null) {
                cacheMap.put(error.getKey().toLowerCase(), error);
            }
        }
    }

    public SystemError getByCode(String key) {
        return key != null ? cacheMap.get(key) : null;
    }

    public SystemError getByErrorCode(ErrorCode errorCode) {
        return errorCode != null ? cacheMap.get(errorCode.name().toLowerCase()) : null;
    }

}
