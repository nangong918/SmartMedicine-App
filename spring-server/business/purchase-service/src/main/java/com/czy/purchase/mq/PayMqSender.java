package com.czy.purchase.mq;

import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/25 18:08
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PayMqSender implements RabbitMqSenderInterface {

    @Override
    public void push(Message message) {

    }

    @Override
    public <T extends BaseResponseData> void push(T t) {
        RabbitMqSenderInterface.super.push(t);
    }
}
