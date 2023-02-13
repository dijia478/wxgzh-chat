package com.dijia478.wxgzh_chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dijia478.wxgzh_chat.entity.MessageResponseBody;
import com.dijia478.wxgzh_chat.entity.MessageSendBody;
import com.dijia478.wxgzh_chat.utils.CacheUtils;
import com.dijia478.wxgzh_chat.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    /**
     * 接口请求地址
     */
    private final String url = "https://api.openai.com/v1/completions";
    /**
     * 定义ai的名字
     */
    private final String Ai = "ChatGPT:";
    @Value("${openapi.key}")
    private String apiKey;

    @Override
    public String reply(String messageContent, String userKey) {
        Integer returnLength = CacheUtils.getOneHours(userKey);
        if (returnLength != null && returnLength > 1000) {
            return "您的OpenAI免费额度1000字节已用完，请24小时后再体验。";
        }

        // 默认信息
        String message = "Human:你好\nChatGPT:你好\n";
        if (CacheUtils.hasKey(messageContent)) {
            return CacheUtils.get(messageContent).replace("ChatGPT:", "").trim();
        }

        if (CacheUtils.hasKey(userKey)) {
            // 如果存在key，拿出来
            message = CacheUtils.get(userKey);
        }
        message = message + "Human:" + messageContent + "\n";

        // 调用接口获取数据
        JSONObject obj = getReplyFromGPT(message);
        MessageResponseBody messageResponseBody = JSONObject.toJavaObject(obj, MessageResponseBody.class);
        // 存储对话内容，让机器人更加智能
        if (messageResponseBody != null) {
            if (!CollectionUtils.isEmpty(messageResponseBody.getChoices())) {
                String replyText = messageResponseBody.getChoices().get(0).getText();
                CacheUtils.set(messageContent, replyText);

                int length = replyText.length();
                Integer oldLength = CacheUtils.getOneHours(userKey);
                if (oldLength != null) {
                    length += oldLength;
                }
                CacheUtils.setOneHours(userKey, length);

                // 拼接字符,设置回去
                String msg = CacheUtils.get(userKey);
                msg = msg + Ai + replyText + "\n";
                CacheUtils.set(userKey, msg);
                return replyText.replace("ChatGPT:", "").trim();
            }
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "OpenAI免费配额已用完，请稍后再试";
    }

    private JSONObject getReplyFromGPT(String message) {
        Map<String, String> header = new HashMap();
        header.put("Authorization", "Bearer " + apiKey);
        header.put("Content-Type", "application/json");
        MessageSendBody messageSendBody = buildConfig();
        messageSendBody.setPrompt(message);
        String body = JSON.toJSONString(messageSendBody, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        log.debug("发送的数据：" + body);
        String data = HttpUtil.doPostJson(url, body, header);
        log.info("响应的数据：" + data);
        return JSON.parseObject(data);
    }

    /**
     * 构建请求体
     *
     * @return
     */
    private MessageSendBody buildConfig() {
        MessageSendBody messageSendBody = new MessageSendBody();
        messageSendBody.setModel("text-davinci-003");
        messageSendBody.setTemperature(0.9);
        messageSendBody.setMaxTokens(1000);
        messageSendBody.setTopP(1);
        messageSendBody.setFrequencyPenalty(0.0);
        messageSendBody.setPresencePenalty(0.6);
        List<String> stop = new ArrayList<>();
        stop.add(" ChatGPT:");
        stop.add(" Human:");
        messageSendBody.setStop(stop);
        return messageSendBody;
    }
}
