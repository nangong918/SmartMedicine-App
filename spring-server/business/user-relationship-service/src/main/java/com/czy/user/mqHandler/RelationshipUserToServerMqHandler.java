package com.czy.user.mqHandler;


import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.event.MessageRouteEvent;
import com.czy.user.component.RelationshipEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
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
/* 取消使用注解创建消息队列，
    原因：1.需要微服务启动才能创建消息队列，容易出现其他需要消息队列的微服务缺失微服务而无法启动.
         2.注解创建是静态的，无法通过nacos等热部署修改配置
 */
/*@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = MqConstants.RelationshipQueue.RELATIONSHIP_TO_SERVICE_QUEUE,
                        // 持久化
                        durable = "true",
                        // 惰性队列
                        arguments = @Argument(name = "x-queue-mode", value = "lazy")
                ),
                exchange = @Exchange(
                        // .topic.exchange
                        value = MqConstants.Exchange.RELATIONSHIP_EXCHANGE,
                        // 此处一定要制定交换机的类型
                        type = ExchangeTypes.TOPIC
                ),
                // 路由键；就是"china.#"
                key = (MqConstants.MessageQueue.Routing.TO_SERVICE_ROUTING + ".#")
        )
)*/
@RequiredArgsConstructor
//@RabbitListener(queues = SocketMessageMqConstant.USER_RECEIVE_QUEUE)
@Component
public class RelationshipUserToServerMqHandler {

    private final ApplicationContext applicationContext;
    private final RelationshipEventManager<Message> eventManager;

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
