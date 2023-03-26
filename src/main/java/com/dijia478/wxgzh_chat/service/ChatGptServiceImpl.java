package com.dijia478.wxgzh_chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dijia478.wxgzh_chat.entity.ChatCompletionChoice;
import com.dijia478.wxgzh_chat.entity.ChatCompletionRequest;
import com.dijia478.wxgzh_chat.entity.ChatMessage;
import com.dijia478.wxgzh_chat.utils.CacheUtils;
import com.dijia478.wxgzh_chat.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dijia478
 */
@Slf4j
@Service
public class ChatGptServiceImpl implements ChatGptService {

    @Value("${openai.url}")
    private String url;

    @Value("${openai.key}")
    private String apiKey;

    @Override
    public String reply(String messageContent, String userKey) {
        // 对用户使用进行限流
        Integer returnLength = CacheUtils.CACHE_1.getIfPresent(userKey);
        if (returnLength != null && returnLength > 2000) {
            return "您的OpenAI免费额度2000字符已用完，请2小时后再体验。";
        }

        // 防止重复发送消息
        if (CacheUtils.CACHE_2.getIfPresent(messageContent) != null) {
            return CacheUtils.CACHE_2.getIfPresent(messageContent);
        }

        // 获取历史对话内容
        List<ChatMessage> messages = CacheUtils.CACHE_3.getIfPresent(userKey);
        messages = messages == null ? new ArrayList<>() : messages;
        messages.add(new ChatMessage("user", messageContent));

        // 调用接口获取数据
        JSONObject jsonObject = getRespFromGPT(messages);
        if (!jsonObject.containsKey("choices")) {
            return "OpenAI服务器发送错误，请稍后再试";
        }
        List<ChatCompletionChoice> choices = jsonObject.getJSONArray("choices").toJavaList(ChatCompletionChoice.class);
        ChatMessage context = new ChatMessage(choices.get(0).getMessage().getRole(), choices.get(0).getMessage().getContent());

        String replyText = context.getContent();
        replyText = StringUtils.removeStart(replyText, "\n");
        replyText = StringUtils.removeEnd(replyText, "\n");

        // 存储请求文本的响应内容
        CacheUtils.CACHE_2.put(messageContent, replyText);

        // 存储用户的已回复字符长度
        int length = replyText.length();
        Integer oldLength = CacheUtils.CACHE_1.getIfPresent(userKey);
        if (oldLength != null) {
            length += oldLength;
        }
        CacheUtils.CACHE_1.put(userKey, length);

        // 存储用户的历史对话内容
        messages.add(context);
        CacheUtils.CACHE_3.put(userKey, messages);
        return replyText;
    }

    private JSONObject getRespFromGPT(List<ChatMessage> messages) {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + apiKey);
        header.put("Content-Type", "application/json");
        ChatCompletionRequest reqBody = buildReqBody(messages);
        String body = JSON.toJSONString(reqBody, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        log.info("请求的数据：" + body);
        String data = HttpUtil.doPostJson(url, body, header);
        log.info("响应的数据：" + data);
        return JSON.parseObject(data);
    }

    /**
     * 构建请求体
     *
     * @return
     */
    private ChatCompletionRequest buildReqBody(List<ChatMessage> messages) {
        return ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(1.0)
                .n(1)
                .stream(false)
                .max_tokens(500)
                .presence_penalty(0.0)
                .frequency_penalty(0.6)
                .user("user")
                .build();
    }

}
