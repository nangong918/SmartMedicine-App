package com.czy.post.mq.sender;


import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.MqConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.converter.base.BaseResponseConverter;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.OssTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/1 13:59
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitMqSender implements RabbitMqSenderInterface {

    private final RabbitTemplate rabbitJsonTemplate;
    private final BaseResponseConverter baseResponseConverter;

    public void pushToOss(OssTask ossTask){
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.OSS_EXCHANGE,
                MqConstants.OssQueue.Routing.TO_SERVICE_ROUTING,
                ossTask
        );
    }

    @Override
    public void push(Message message){
        if (message == null){
            return;
        }
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.POST_EXCHANGE,
                MqConstants.PostQueue.Routing.TO_SOCKET_ROUTING,
                message);
    }


    /**
     * 转换并发送
     * @param t     继承BaseResponseData的t
     */
    @Override
    public <T extends  BaseResponseData> void push(T t){
        Message message = t.getMessageByResponse();
        message.setType(MessageTypeTranslator.translateClean(t.getType()));
        if (ResponseMessageType.NULL.equals(message.getType())){
            return;
        }

        push(message);
    }
    
}
