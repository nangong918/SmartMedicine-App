package com.czy.api.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 13225
 */
@Data
public class MessageEntity {
    @JsonProperty("content")
    public String content;
    @JsonProperty("timestamp")
    public Long timestamp;
    @JsonProperty("messageId")
    public String messageId;
}
