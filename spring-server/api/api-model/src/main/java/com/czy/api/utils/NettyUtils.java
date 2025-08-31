package com.czy.api.utils;

import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.entity.event.Message;
import exception.AppException;
import exception.ExceptionEnums;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/7/25 18:30
 */
@Slf4j
public class NettyUtils {
    public static void sendErrorMessage(Long senderId, ExceptionEnums exceptionEnums, RabbitMqSenderInterface rabbitMqSender){
        if (senderId == null){
            log.warn("sendErrorMessage 发送者id为空");
            return;
        }
        Message message = new Message();
        message.setSenderId(NettyConstants.SERVER_ID);
        // 通知发送者操作失败
        message.setReceiverId(senderId);
        message.setType(ResponseMessageType.Error.NETTY_ERROR);
        message.setData(exceptionEnums.getDataMap());
        // Mq -> sender
        rabbitMqSender.push(message);
    }

    public static void sendErrorMessage(Long senderId, String errorMessage, RabbitMqSenderInterface rabbitMqSender){
        if (senderId == null){
            log.warn("sendErrorMessage 发送者id为空");
            return;
        }
        Message message = new Message();
        message.setSenderId(NettyConstants.SERVER_ID);
        // 通知发送者操作失败
        message.setReceiverId(senderId);
        message.setType(ResponseMessageType.Error.NETTY_ERROR);
        Map<String, String> data = new HashMap<>();
        data.put("code", "600");
        data.put("message", errorMessage);
        message.setData(data);
        // Mq -> sender
        rabbitMqSender.push(message);
    }

    public static void sendErrorMessage(Long senderId, AppException e, RabbitMqSenderInterface rabbitMqSender){
        if (senderId == null){
            log.warn("sendErrorMessage 发送者id为空");
            return;
        }
        Message message = new Message();
        message.setSenderId(NettyConstants.SERVER_ID);
        // 通知发送者操作失败
        message.setReceiverId(senderId);
        message.setType(ResponseMessageType.Error.NETTY_ERROR);

        if (e.getExceptionEnums() != null){
            message.setData(e.getExceptionEnums().getDataMap());
        }
        else {
            Map<String, String> data = new HashMap<>();
            data.put("code", Optional.ofNullable(e.getErrCode()).orElse(""));
            data.put("message", Optional.ofNullable(e.getMessage()).orElse(""));
            message.setData(data);
        }
        // Mq -> sender
        rabbitMqSender.push(message);
    }

    public static void sendSuccessMessage(Long senderId, String successContent, RabbitMqSenderInterface rabbitMqSender) {
        if (senderId == null){
            log.warn("sendSuccessMessage 发送者id为空");
            return;
        }
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

    public static void sendSuccessMessage(Long senderId, Map<String, String> dataMap, RabbitMqSenderInterface rabbitMqSender){
        if (senderId == null){
            log.warn("sendSuccessMessage 发送者id为空");
            return;
        }
        Message message = new Message();
        message.setSenderId(NettyConstants.SERVER_ID);
        // 通知发送者操作成功
        message.setReceiverId(senderId);
        message.setType(ResponseMessageType.Success.NETTY_SUCCESS);
        Map<String, String> data = Optional.ofNullable(dataMap)
                        .orElse(new HashMap<>());
        data.put("code", NettyConstants.SUCCESS_CODE);
        message.setData(data);
        // Mq -> sender
        rabbitMqSender.push(message);
    }
}
