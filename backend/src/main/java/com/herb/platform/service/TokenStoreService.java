package com.herb.platform.service;

import java.util.concurrent.TimeUnit;

/**
 * Token存储服务接口
 * 支持Redis和内存两种实现方式
 */
public interface TokenStoreService {

    /**
     * 存储Token
     *
     * @param key Token键
     * @param value Token值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, String value, long timeout, TimeUnit unit);

    /**
     * 获取Token
     *
     * @param key Token键
     * @return Token值
     */
    String get(String key);

    /**
     * 删除Token
     *
     * @param key Token键
     */
    void delete(String key);

    /**
     * 检查Token是否存在
     *
     * @param key Token键
     * @return 是否存在
     */
    default boolean exists(String key) {
        return get(key) != null;
    }
}
