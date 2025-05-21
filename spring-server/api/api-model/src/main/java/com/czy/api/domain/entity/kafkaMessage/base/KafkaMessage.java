package com.czy.api.domain.entity.kafkaMessage.base;

import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/21 15:53
 */
@Data
public class KafkaMessage implements BaseBean {
    private Long id;
    private Long timestamp;
}
