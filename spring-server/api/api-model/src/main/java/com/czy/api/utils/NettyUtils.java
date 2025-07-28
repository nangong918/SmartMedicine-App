package com.czy.api.utils;

import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.entity.event.Message;
import exception.ExceptionEnums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/7/25 18:30
 */
public class NettyUtils {
    public static void sentErrorMessage(Long senderId, ExceptionEnums exceptionEnums, RabbitMqSenderInterface rabbitMqSender){
        Message message = new Message();
        message.setSenderId(NettyConstants.SERVER_ID);
        // 通知发送者操作失败
        message.setReceiverId(senderId);
        message.setType(ResponseMessageType.Error.NETTY_ERROR);
        message.setData(exceptionEnums.getDataMap());
        // Mq -> sender
        rabbitMqSender.push(message);
    }

    public static void sendSuccessMessage(Long senderId, String successContent, RabbitMqSenderInterface rabbitMqSender) {
        Message message = new Message();
        message.setSenderId(NettyConstants.SERVER_ID);
        // 通知发送者操作成功
        message.setReceiverId(senderId);
        message.setType(ResponseMessageType.Success.NETTY_SUCCESS);
        Map<String, String> data = new HashMap<>();
        data.put("code", NettyConstants.SUCCESS_CODE);
        data.put("message", successContent);
        message.setData(data);
        // Mq -> sender
        rabbitMqSender.push(message);
    }
}
