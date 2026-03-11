package com.herb.platform.service.impl;

import com.herb.platform.service.TokenStoreService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class InMemoryTokenStoreService implements TokenStoreService {

    private final Map<String, TokenEntry> store = new ConcurrentHashMap<>();

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        long expireAt = Instant.now().plusMillis(unit.toMillis(timeout)).toEpochMilli();
        store.put(key, new TokenEntry(value, expireAt));
    }

    @Override
    public String get(String key) {
        TokenEntry entry = store.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.expireAt < System.currentTimeMillis()) {
            store.remove(key);
            return null;
        }
        return entry.value;
    }

    @Override
    public void delete(String key) {
        store.remove(key);
    }

    private static final class TokenEntry {
        private final String value;
        private final long expireAt;

        private TokenEntry(String value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }
    }
}
