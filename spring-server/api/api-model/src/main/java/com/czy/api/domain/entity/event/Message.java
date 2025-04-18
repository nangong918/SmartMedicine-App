package com.czy.api.domain.entity.event;


import json.BaseBean;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/2/12 0:30
 */

@Data
public class Message implements BaseBean {
    // 1.实现Message。2.实现其他类型的Message。3.Message用ProtoBuf序列化
    // 4.Message用MyBatis存入数据库。5.Message用Redis缓存。6.Message用RabbitMQ发送
    @NotBlank(message = "Sender Account cannot be null or empty")
    private String senderId;
    @NotBlank(message = "receiver Account cannot be null or empty")
    private String receiverId;
    @NotBlank(message = "Message type cannot be null or empty")
    private String type;
    private Map<String, String> data;
    private Long timestamp = System.currentTimeMillis();

    /**
     * 无参构造器
     */
    public Message(){
        nonNull();
    }

    /**
     * 克隆构造器
     * @param message   待克隆的Message
     */
    public Message(Message message){
        if (message == null){
            nonNull();
            return;
        }
        this.senderId = message.getSenderId();
        this.receiverId = message.getReceiverId();
        this.type = message.getType();
        this.data = new HashMap<>(message.getData());
        this.timestamp = message.getTimestamp();
    }

    // 非空化
    public void nonNull(){
        if (this.senderId == null){
            this.senderId = "";
        }
        if (this.receiverId == null){
            this.receiverId = "";
        }
        if (this.type == null){
            this.type = "";
        }
        if (this.data == null){
            this.data = new HashMap<>();
        }
        if (this.timestamp == null){
            this.timestamp = System.currentTimeMillis();
        }
    }
}
