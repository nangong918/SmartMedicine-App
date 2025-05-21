package com.czy.api.domain.entity.kafkaMessage.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/5/21 15:53
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserActionMessage extends KafkaMessage{
    private Long userId;
}
