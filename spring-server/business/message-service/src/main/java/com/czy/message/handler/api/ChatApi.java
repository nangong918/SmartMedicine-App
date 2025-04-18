package com.czy.message.handler.api;


import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.http.request.SendImageRequest;
import com.czy.api.domain.dto.http.request.SendTextDataRequest;
import com.czy.message.annotation.MessageType;

/**
 * @author 13225
 * @date 2025/3/10 17:00
 */
public interface ChatApi {

    @MessageType(value = RequestMessageType.Chat.SEND_TEXT_MESSAGE_TO_USER, desc = "发送文本消息")
    void sendTextMessageToUser(SendTextDataRequest sendTextDataRequest);

    @MessageType(value = RequestMessageType.Chat.SEND_IMAGE_MESSAGE_TO_USER, desc = "发送图片消息")
    void sendImageToUser(SendImageRequest request);
}
