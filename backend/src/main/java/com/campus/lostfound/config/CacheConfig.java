package com.campus.lostfound.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * In-memory cache fallback when Redis is not available.
 * Replaces RedisTemplate with a simple ConcurrentHashMap.
 */
@Slf4j
@Configuration
public class CacheConfig {

    @Bean
    public CacheService cacheService() {
        log.info("Using in-memory cache (Redis not available)");
        return new InMemoryCacheService();
    }

    public interface CacheService {
        void set(String key, Object value, long timeout, TimeUnit unit);
        Object get(String key);
        void delete(String key);
    }

    static class InMemoryCacheService implements CacheService {

        private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

        @Override
        public void set(String key, Object value, long timeout, TimeUnit unit) {
            long expireAt = System.currentTimeMillis() + unit.toMillis(timeout);
            cache.put(key, new CacheEntry(value, expireAt));
        }

        @Override
        public Object get(String key) {
            CacheEntry entry = cache.get(key);
            if (entry == null) {
                return null;
            }
            if (System.currentTimeMillis() > entry.expireAt) {
                cache.remove(key);
                return null;
            }
            return entry.value;
        }

        @Override
        public void delete(String key) {
            cache.remove(key);
        }

        static class CacheEntry {
            final Object value;
            final long expireAt;

            CacheEntry(Object value, long expireAt) {
                this.value = value;
                this.expireAt = expireAt;
            }
        }
    }
}
