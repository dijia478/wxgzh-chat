package com.dijia478.wxgzh_chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dijia478.wxgzh_chat.entity.MessageResponseBody;
import com.dijia478.wxgzh_chat.entity.MessageSendBody;
import com.dijia478.wxgzh_chat.utils.CacheUtils;
import com.dijia478.wxgzh_chat.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        Integer returnLength = CacheUtils.getOneDay(userKey);
        if (returnLength != null && returnLength > 3000) {
            return "您的OpenAI免费额度1000字节已用完，请2小时后再体验。";
        }

        // 默认信息
        String message = "Human:你好\nChatGPT:你好\n";
        if (CacheUtils.hasKey(messageContent)) {
            return CacheUtils.get(messageContent);
        }

        if (CacheUtils.hasKey(userKey)) {
            // 如果存在key，拿出来
            message = CacheUtils.get(userKey);
        }
        message = message + "Human:" + messageContent + "\n";
        if (message.length() > 2000) {
            return "您的对话太过频繁，请3分钟后再试。";
        }

        // 调用接口获取数据
        JSONObject obj = getReplyFromGPT(message);
        MessageResponseBody messageResponseBody = JSONObject.toJavaObject(obj, MessageResponseBody.class);
        // 存储对话内容，让机器人更加智能
        if (messageResponseBody != null) {
            if (!CollectionUtils.isEmpty(messageResponseBody.getChoices())) {
                String replyText = messageResponseBody.getChoices().get(0).getText();
                replyText = StringUtils.removeStart(replyText, "\n");
                replyText = StringUtils.removeStart(replyText, "Null");
                replyText = StringUtils.removeStart(replyText, "ChatGPT:");
                replyText = StringUtils.removeStart(replyText, "ChatGPT：");
                replyText = StringUtils.removeStart(replyText, "GPT:");
                replyText = StringUtils.removeStart(replyText, "GPT：");
                replyText = StringUtils.removeStart(replyText, "GPT-3:");
                replyText = StringUtils.removeStart(replyText, "GPT-3：");
                replyText = StringUtils.removeStart(replyText, "\n");

                CacheUtils.set(messageContent, replyText);

                int length = replyText.length();
                Integer oldLength = CacheUtils.getOneDay(userKey);
                if (oldLength != null) {
                    length += oldLength;
                }
                CacheUtils.setOneDay(userKey, length);

                // 拼接字符,设置回去
                message = message + Ai + replyText + "\n";
                CacheUtils.set(userKey, message);
                return replyText;
            }
        }
        if (obj.toJSONString().contains("You exceeded your current quota")) {
            return "系统OpenAI免费配额已用完，请稍后再试";
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "OpenAI服务器错误，请稍后再试";
    }

    private JSONObject getReplyFromGPT(String message) {
        Map<String, String> header = new HashMap();
        header.put("Authorization", "Bearer " + apiKey);
        header.put("Content-Type", "application/json");
        MessageSendBody messageSendBody = buildConfig();
        messageSendBody.setPrompt(message);
        String body = JSON.toJSONString(messageSendBody, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        log.info("请求的数据：" + message);
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
