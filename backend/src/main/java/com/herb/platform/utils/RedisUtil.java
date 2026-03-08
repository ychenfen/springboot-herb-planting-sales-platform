package com.herb.platform.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== 通用操作 ====================

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("设置过期时间失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 获取过期时间
     */
    public long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire != null ? expire : -1;
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断key是否存在失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("删除缓存失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 批量删除缓存
     */
    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("批量删除缓存失败", e);
            return 0;
        }
    }

    // ==================== String类型操作 ====================

    /**
     * 获取缓存
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置缓存并设置过期时间
     */
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 递增
     */
    public long increment(String key, long delta) {
        Long result = redisTemplate.opsForValue().increment(key, delta);
        return result != null ? result : 0;
    }

    /**
     * 递减
     */
    public long decrement(String key, long delta) {
        Long result = redisTemplate.opsForValue().decrement(key, delta);
        return result != null ? result : 0;
    }

    // ==================== Hash类型操作 ====================

    /**
     * HashGet
     */
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取所有Hash值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("HashSet失败: key={}", key, e);
            return false;
        }
    }

    /**
     * Hash设置单个字段
     */
    public boolean hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            log.error("Hash设置失败: key={}, field={}", key, field, e);
            return false;
        }
    }

    /**
     * Hash删除字段
     */
    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 判断Hash中是否存在字段
     */
    public boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ==================== Set类型操作 ====================

    /**
     * 获取Set中的所有值
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("获取Set失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 判断Set中是否存在某值
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("判断Set是否存在值失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 将数据放入Set
     */
    public long sSet(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Set添加失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * 移除Set中的值
     */
    public long sRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Set移除失败: key={}", key, e);
            return 0;
        }
    }

    // ==================== List类型操作 ====================

    /**
     * 获取List指定范围的内容
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("获取List失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取List长度
     */
    public long lSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取List长度失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * 将值放入List头部
     */
    public boolean lLeftPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("List添加失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 将值放入List尾部
     */
    public boolean lRightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("List添加失败: key={}", key, e);
            return false;
        }
    }
}
