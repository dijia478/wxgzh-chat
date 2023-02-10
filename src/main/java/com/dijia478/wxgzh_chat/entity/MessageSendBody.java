package com.dijia478.wxgzh_chat.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author dijia478
 */
@Data
public class MessageSendBody {

    List<String> stop;
    private String model;
    private String prompt;
    private double temperature;
    @JSONField(name = "max_tokens")
    private int maxTokens;
    @JSONField(name = "top_p")
    private int topP;
    @JSONField(name = "frequency_penalty")
    private double frequencyPenalty;
    @JSONField(name = "presence_penalty")
    private double presencePenalty;

}
