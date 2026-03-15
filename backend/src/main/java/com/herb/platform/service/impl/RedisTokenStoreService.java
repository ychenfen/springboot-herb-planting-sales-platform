package com.herb.platform.service.impl;

import com.herb.platform.service.TokenStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的Token存储服务实现
 * 当Redis可用时优先使用此实现
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@ConditionalOnBean(StringRedisTemplate.class)
public class RedisTokenStoreService implements TokenStoreService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis存储Token失败，key={}", key, e);
        }
    }

    @Override
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis获取Token失败，key={}", key, e);
            return null;
        }
    }

    @Override
    public void delete(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis删除Token失败，key={}", key, e);
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            Boolean result = stringRedisTemplate.hasKey(key);
            return result != null && result;
        } catch (Exception e) {
            log.warn("Redis检查Token失败，key={}", key, e);
            return false;
        }
    }
}
