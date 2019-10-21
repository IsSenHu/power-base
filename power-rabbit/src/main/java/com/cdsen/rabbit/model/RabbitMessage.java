package com.cdsen.rabbit.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/10/21 14:49
 */
@Data
public class RabbitMessage<T> implements Serializable {
    private Long userId;
    private String id;
    private T data;

    public RabbitMessage() {
    }

    public RabbitMessage(Long userId, String id, T data) {
        this.userId = userId;
        this.id = id;
        this.data = data;
    }
}
