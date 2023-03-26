package com.dijia478.wxgzh_chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dijia478
 * @date 2023/3/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    /** 消息角色 */
    private String role;

    /** 消息内容 */
    private String content;

}
