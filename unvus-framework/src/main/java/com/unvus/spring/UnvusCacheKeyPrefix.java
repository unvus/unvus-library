package com.unvus.spring;

import org.springframework.data.redis.cache.CacheKeyPrefix;

public class UnvusCacheKeyPrefix implements CacheKeyPrefix {
    private final String prefix;
    private final String delimiter = ":";

    public UnvusCacheKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String compute(String cacheName) {
        return prefix.concat(delimiter).concat(cacheName).concat(delimiter);
    }
}
