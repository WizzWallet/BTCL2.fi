package com.wizz.fi.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 尝试获取锁
     *
     * @param key     锁的键
     * @param value   锁的唯一标识
     * @param timeout 过期时间（毫秒）
     * @return 是否获取到锁
     */
    public boolean tryLock(String key, String value, long timeout) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.MILLISECONDS);
        return success != null && success;
    }

    /**
     * 释放锁
     *
     * @param key   锁的键
     * @param value 锁的唯一标识
     */
    public void unlock(String key, String value) {
        String currentValue = (String) redisTemplate.opsForValue().get(key);
        if (value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }
}
