package com.czy.message.mqHandler;


import com.czy.api.constant.mq.SocketMessageMqConstant;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.event.MessageRouteEvent;

import com.czy.message.component.MessageEventManager;
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
 * @date 2025/3/18 10:20
 * 事件驱动的本质是一对多，一个消息可以被多个Listener接收，是松耦合；而不是根据消息类型进行单点传递，这是强耦合。
 */

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = SocketMessageMqConstant.USER_RECEIVE_QUEUE)
public class MessageUserToServerMqHandler {

    private final ApplicationContext applicationContext;
    private final MessageEventManager<Message> eventManager;

    @RabbitHandler
    public void handleMessage(@Valid Message userReceivedMessage) {
        // 事件驱动处理消息
        boolean isToThisServiceMessage = checkIsToThisServiceMessage(userReceivedMessage.getType());
        if (isToThisServiceMessage){
            // 第一份交给消息路由
            applicationContext.publishEvent(new MessageRouteEvent(userReceivedMessage));
        }
        // 第二份交给大数据平台 (大数据平台自己去监听)
//        applicationContext.publishEvent(new BigDataEvent(userReceivedMessage));
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
/**
 * 关于事件驱动架构设计：
 * 一个消息可以交给多个监听者异步处理
 * 转发推送服务，存储服务，统计服务？
 * 存储服务：每个消息的存储方式都不同，还不能直接存储，但是消息可以缓存。
 * 比如说现在一个消息会影响两个监听者？
 * 用户大数据收集监听者：用户最近活跃时间，用户活跃的频率。
 */
