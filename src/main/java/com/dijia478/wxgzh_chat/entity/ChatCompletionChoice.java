package com.dijia478.wxgzh_chat.entity;

import lombok.Data;

/**
 * 用于接收ChatGPT返回的数据
 *
 * @author dijia478
 * @date 2023/3/26
 */
@Data
public class ChatCompletionChoice {

    Integer index;

    ChatMessage message;

    String finishReason;

}
