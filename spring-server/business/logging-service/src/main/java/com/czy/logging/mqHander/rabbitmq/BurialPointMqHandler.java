package com.czy.logging.mqHander.rabbitmq;

import com.czy.api.constant.mq.SocketMessageMqConstant;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.event.BigDataEvent;
import com.czy.logging.component.LoggingEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/5/23 16:47
 */

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = SocketMessageMqConstant.USER_RECEIVE_QUEUE)
public class BurialPointMqHandler {

    private final ApplicationContext applicationContext;
    private final LoggingEventManager<Message> eventManager;

    @RabbitHandler
    public void handleMessage(@Valid Message loggingMessage) {
        boolean isToThisServiceMessage = checkIsToThisServiceMessage(loggingMessage.getType());
        if (isToThisServiceMessage){

            // 交给大数据logging-service
            applicationContext.publishEvent(new BigDataEvent(loggingMessage));
        }
    }


    private boolean checkIsToThisServiceMessage(String messageType){
        if (!StringUtils.hasText(messageType)){
            return false;
        }
        boolean isToThisServiceMessage = false;
        for (String handlers : eventManager.getMessageHandlers()){
            if (messageType.equals(handlers)){
                isToThisServiceMessage = true;
                break;
            }
        }
        return isToThisServiceMessage;
    }

}
