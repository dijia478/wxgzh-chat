package com.dijia478.wxgzh_chat.entity;

import lombok.Data;

import java.util.List;

/**
 * @author dijia478
 */
@Data
public class MessageResponseBody {

    private String id;

    private String object;

    private int create;

    private String model;

    private List<Choices> choices;

}
