package com.herb.platform.service;

import java.util.concurrent.TimeUnit;

public interface TokenStoreService {

    void set(String key, String value, long timeout, TimeUnit unit);

    String get(String key);

    void delete(String key);
}
