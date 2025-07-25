package com.czy.api.utils;

import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.entity.event.Message;
import exception.ExceptionEnums;

/**
 * @author 13225
 * @date 2025/7/25 18:30
 */
public class NettyUtils {
    public static void sentErrorMessage(Long senderId, ExceptionEnums exceptionEnums, RabbitMqSenderInterface rabbitMqSender){
        Message message = new Message();
        message.setSenderId(NettyConstants.SERVER_ID);
        // 通知发送者删除失败
        message.setReceiverId(senderId);
        message.setType(ResponseMessageType.Error.NETTY_ERROR);
        message.setData(exceptionEnums.getDataMap());
        // Mq -> sender
        rabbitMqSender.push(message);
    }
}
