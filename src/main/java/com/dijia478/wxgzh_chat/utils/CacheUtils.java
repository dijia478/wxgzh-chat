package com.dijia478.wxgzh_chat.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CacheUtils {

    private static final Cache<String, String> CACHE = Caffeine.newBuilder()
            .initialCapacity(10000)
            .maximumSize(100000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    private static final Cache<String, Integer> CACHE_ONE_DAY = Caffeine.newBuilder()
            .initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build();

    public static void setOneDay(String key, Integer value) {
        CACHE_ONE_DAY.put(key, value);
    }


    public static Integer getOneDay(String key) {
        return CACHE_ONE_DAY.getIfPresent(key);
    }

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
