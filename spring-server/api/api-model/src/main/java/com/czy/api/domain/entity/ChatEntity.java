package com.czy.api.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 13225
 */
@Data
public class ChatEntity {

    // 消息类容
    @JsonProperty("message")
    public MessageEntity message;

    // 发送者Account
    @JsonProperty("senderAccount")
    public String senderAccount;

    // 接收者Account
    @JsonProperty("receiverAccount")
    public String receiverAccount;

    // 消息时间
    @JsonProperty("timestamp")
    public Long timestamp;

}
