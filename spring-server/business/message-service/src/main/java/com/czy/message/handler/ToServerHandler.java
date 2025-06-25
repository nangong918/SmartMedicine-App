package com.czy.message.handler;


import com.czy.api.api.message.ChatService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.dto.socket.request.HaveReadMessageRequest;
import com.czy.api.domain.dto.socket.response.HaveReadMessageResponse;
import com.czy.api.domain.entity.event.Message;
import com.czy.springUtils.annotation.HandlerType;
import com.czy.message.mq.sender.RabbitMqSender;
import com.czy.message.handler.api.ToServiceApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
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
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    // ping -> pong [方法废弃：在netty sdk中完成]
    @Deprecated
    public void handlePing(Message ping){
        Message pong = new Message(ping);
        String pongType = MessageTypeTranslator.translate(ping.getType());
        pong.setType(pongType);
        pong.setReceiverId(ping.getSenderId());
        pong.setSenderId(NettyConstants.SERVER_ID);

        rabbitMqSender.push(pong);
    }

    public void handleReadMessage(HaveReadMessageRequest request){
        if (request == null){
            log.warn("HaveReadMessageRequest is null");
            return;
        }
        // 特别注意此时的发送者其实是 已读那条消息接收者；也就是说receiverUserAccount才是已读那条消息的发送者
        // 特别注意，此处的receiverId = SERVER_ID;
        chatService.clearUserChatMessageUnreadCount(request.getSenderId(), request.getReceiverId());

        // push告诉另一个人已经被清理了
        HaveReadMessageResponse response = new HaveReadMessageResponse();
        // 通过请求初始化
        response.initResponseByRequest(request);
        // 特殊处理：此处单独设置
        response.setSenderId(NettyConstants.SERVER_ID);
        response.setReceiverId(request.getSenderId());
        // 属性值
        UserDo receiverDo = userService.getUserById(request.getSenderId());
        response.receiverAccount = receiverDo.getAccount();
        rabbitMqSender.push(response);
    }
}
