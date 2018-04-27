package com.yiban.erp.service.util;

import com.yiban.erp.dao.BizLockMapper;
import com.yiban.erp.entities.BizLock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LockService {

    private static final Logger logger = LoggerFactory.getLogger(LockService.class);

    private static final int RETYPE_TIMES = 5; //重试次数，如果还没有获取到，认为获取失败
    private static final long WAIT_TIME = 400; //每次重试的等待时间间隔

    @Autowired
    private BizLockMapper bizLockMapper;

    /**
     * 获取锁
     * @param key 锁的key值，必输
     * @return
     */
    public boolean lock(String key) {
        if (StringUtils.isEmpty(key)) {
            logger.error("get lock key must not empty.");
            throw new IllegalArgumentException("lock key must not null");
        }
        int count = 0;
        while (count < RETYPE_TIMES) {
            try {
                count ++;
                BizLock lock = new BizLock();
                lock.setLockKey(key);
                lock.setCreatedTime(new Date());
                int result = bizLockMapper.insert(lock);
                if (result > 0) {
                    return true;
                }
                Thread.sleep(WAIT_TIME);
            }catch (DuplicateKeyException e) {
                logger.warn("lock key is exist. try again. count:{}", count);
            } catch (InterruptedException e) {
                logger.error("GET InterruptedException", e);
            }
        }
        return false;
    }

    /**
     * 释放锁
     * @param key
     */
    public void closeLock(String key) {
        int result = bizLockMapper.deleteByLockKey(key);
        if (result < 0) {
            logger.warn("lock key:{} close fail.", key);
        }
    }

}
