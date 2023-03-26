package com.dijia478.wxgzh_chat.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 用于发送的请求的参数实体类
 *
 * @author dijia478
 * @date 2023/3/26
 */
@Data
@Builder
public class ChatCompletionRequest {

    /** 选择使用的模型，如gpt-3.5-turbo */
    private String model;

    /** 发送的消息列表 */
    private List<ChatMessage> messages;

    /** 温度，默认1，介于 0 和 2 之间。较高的值（如 0.8）将使输出更加随机，而较低的值（如 0.2）将使输出更加集中和确定。 */
    private Double temperature;

    /** 回复条数，一次对话回复的条数 */
    private Integer n;

    /** 是否流式处理，就像ChatGPT一样的处理方式，会增量的发送信息。 */
    private Boolean stream;

    /** 生成的答案允许的最大token数 */
    private Integer max_tokens;

    /** 存在惩罚，默认0，-2.0 和 2.0 之间的数字。正值会根据到目前为止是否出现在文本中来惩罚新标记，从而增加模型谈论新主题的可能性。 */
    private Double presence_penalty;

    /** 频率惩罚，默认0，-2.0 和 2.0 之间的数字。正值会根据新标记在文本中的现有频率对其进行惩罚，从而降低模型逐字重复同一行的可能性。 */
    private Double frequency_penalty;

    /** 对话用户 */
    private String user;

}
