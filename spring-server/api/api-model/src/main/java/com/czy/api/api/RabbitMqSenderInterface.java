package com.czy.api.api;

import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.entity.event.Message;

/**
 * @author 13225
 * @date 2025/7/25 18:32
 */
public interface RabbitMqSenderInterface {
    void push(Message message);
    /**
     * 转换并发送
     * @param t     继承BaseResponseData的t
     */
    default <T extends BaseResponseData> void push(T t){
        Message message = t.getMessageByResponse();
        message.setType(MessageTypeTranslator.translateClean(t.getType()));
        if (ResponseMessageType.NULL.equals(message.getType())){
            return;
        }

        push(message);
    };
}
