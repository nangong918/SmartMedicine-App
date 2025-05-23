package com.czy.post.component;


import com.czy.api.constant.mq.PostMqConstant;
import com.czy.api.constant.mq.SocketMessageMqConstant;
import com.czy.api.constant.netty.MessageTypeTranslator;
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
public class RabbitMqSender {

    private final RabbitTemplate rabbitTemplate;
    private final BaseResponseConverter baseResponseConverter;

    public void pushToOss(OssTask ossTask){
        rabbitTemplate.convertAndSend(PostMqConstant.SERVICE_TO_OSS_QUEUE, ossTask);
    }

    public void push(Message message){
        if (message == null){
            return;
        }
        rabbitTemplate.convertAndSend(
                SocketMessageMqConstant.USER_RECEIVE_QUEUE,
                message);
    }


    /**
     * 转换并发送
     * @param baseResponseData
     */
    public void push(BaseResponseData baseResponseData){
        Message message = baseResponseConverter.getMessage(baseResponseData);
        message.setType(MessageTypeTranslator.translateClean(baseResponseData.getType()));
        if (ResponseMessageType.NULL.equals(message.getType())){
            return;
        }
        rabbitTemplate.convertAndSend(
                SocketMessageMqConstant.USER_RECEIVE_QUEUE,
                message);
    }
    
}
