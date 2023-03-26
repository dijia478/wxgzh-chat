package com.dijia478.wxgzh_chat.utils;

import com.dijia478.wxgzh_chat.entity.ChatMessage;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheUtils {

    /** key是用户id，value是已经返回的字符长度，用于限流 */
    public static final Cache<String, Integer> CACHE_1 = Caffeine.newBuilder()
            .initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    /** key是用户发送的消息，value是该消息的响应，用于防止有些问题重复问，无故消耗token */
    public static final Cache<String, String> CACHE_2 = Caffeine.newBuilder()
            .initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    /** key是用户id，value是之前的历史对话内容，用于实现连续对话 */
    public static final Cache<String, List<ChatMessage>> CACHE_3 = Caffeine.newBuilder()
            .initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

}
