package com.czy.api.api;

import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.entity.event.Message;

/**
 * @author 13225
 * @date 2025/7/25 18:32
 */
public interface RabbitMqSenderInterface {
    void push(Message message);
    <T extends BaseResponseData> void push(T t);
}
