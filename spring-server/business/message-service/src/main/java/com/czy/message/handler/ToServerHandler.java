package com.czy.message.handler;


import com.czy.api.api.message.ChatService;
import com.czy.api.constant.netty.Constants;
import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.socket.request.HaveReadMessageRequest;
import com.czy.api.domain.dto.socket.response.HaveReadMessageResponse;
import com.czy.api.domain.entity.event.Message;
import com.czy.message.annotation.HandlerType;
import com.czy.message.component.RabbitMqSender;
import com.czy.message.handler.api.ToServiceApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/3/11 15:15
 */
@HandlerType(RequestMessageType.ToServer.root)
@Slf4j
@RequiredArgsConstructor
@Component
public class ToServerHandler implements ToServiceApi {

    private final RabbitMqSender rabbitMqSender;
    private final ChatService chatService;

    // ping -> pong [方法废弃：在netty sdk中完成]
    public void handlePing(Message ping){
        Message pong = new Message(ping);
        String pongType = MessageTypeTranslator.translate(ping.getType());
        pong.setType(pongType);
        pong.setReceiverId(ping.getSenderId());
        pong.setSenderId(Constants.SERVER_ID);

        rabbitMqSender.push(pong);
    }

    public void handleReadMessage(HaveReadMessageRequest request){
        if (request == null){
            log.warn("HaveReadMessageRequest is null");
            return;
        }
        // 特别注意此时的发送者其实是 已读那条消息接收者；也就是说receiverUserAccount才是已读那条消息的发送者
        String senderAccount = request.receiverUserAccount;
        // 特别注意，此处的receiverId = SERVER_ID;
        String receiverAccount = request.senderId;
        chatService.clearUserChatMessageUnreadCount(senderAccount, receiverAccount);

        // push告诉另一个人已经被清理了
        HaveReadMessageResponse response = new HaveReadMessageResponse();
        // 通过请求初始化
        response.initResponseByRequest(request);
        // 特殊处理：此处单独设置
        response.senderId = Constants.SERVER_ID;
        response.receiverId = senderAccount;
        // 属性值
        response.receiverAccount = receiverAccount;
        rabbitMqSender.push(response);
    }
}
