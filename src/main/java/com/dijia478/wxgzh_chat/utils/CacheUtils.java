package com.dijia478.wxgzh_chat.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CacheUtils {

    private static final Cache<String, String> CACHE = Caffeine.newBuilder()
            .initialCapacity(10000)
            .maximumSize(100000)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    public static boolean hasKey(String key) {
        return CACHE.getIfPresent(key) != null;
    }


    public static void set(String key, String value) {
        CACHE.put(key, value);
    }


    public static String get(String key) {
        return CACHE.getIfPresent(key);
    }

}
