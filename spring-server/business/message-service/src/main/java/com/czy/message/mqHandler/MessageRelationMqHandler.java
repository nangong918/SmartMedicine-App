package com.czy.message.mqHandler;


import com.czy.api.constant.mq.RelationshipMqConstant;
import com.czy.api.domain.entity.event.RelationshipDelete;
import com.czy.message.mapper.mongo.UserChatMessageMongoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/3/18 10:20
 * 事件驱动的本质是一对多，一个消息可以被多个Listener接收，是松耦合；而不是根据消息类型进行单点传递，这是强耦合。
 */

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = RelationshipMqConstant.RELATION_SEND_MESSAGE_QUEUE)
public class MessageRelationMqHandler {

    private final UserChatMessageMongoMapper userChatMessageMongoMapper;

    @RabbitHandler
    public void handleMessage(@Valid RelationshipDelete message) {
        // 事件驱动处理消息
        // 删除聊天记录
        userChatMessageMongoMapper.deleteAllMessages(message.getSenderId(), message.getReceiverId());
    }

}

