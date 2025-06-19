/*
package com.czy.netty.mq.handler;


import com.czy.api.constant.mq.SocketMessageMqConstant;
import com.czy.api.domain.entity.event.Message;
import com.czy.netty.component.ToClientMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

*/
/**
 * @author 13225
 * @date 2025/3/18 10:20
 *//*


@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = SocketMessageMqConstant.USER_SEND_QUEUE)
public class UserMqHandler {

    private final ToClientMessageSender messageSender;

    */
/**
     * TODO 将Dubbo和Mq的异常通过RpcException告诉调用方。并且思考Valid注解这种的异常怎么也告诉调用方？
     *      1. Dubbo的调用失败告诉调用方
     *      2. Mq的消息异常交给死信队列
     * @param userSendMessage   消息
     *//*

    @RabbitHandler
    public void handleMessage(@Valid Message userSendMessage) {
        // 监听到消息校验之后就发送
        messageSender.pushToClient(userSendMessage);
    }
}
*/
