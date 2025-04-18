package com.czy.netty.event;


import com.czy.api.constant.netty.Constants;
import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.converter.base.MessageConverter;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.model.RequestBodyProto;
import com.czy.netty.channel.ChannelManager;
import com.czy.netty.component.RabbitMqSender;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 13225
 * @date 2025/3/28 18:25
 * 微服务事件管理者
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SpringCloudEventManager {

    private final RabbitMqSender rabbitMqSender;
    private final MessageConverter messageConverter;
    private final ChannelManager channelManager;

    public void process(Channel channel, RequestBodyProto.RequestBody request){
        // 校验channel
        if (channel == null){
            return;
        }
        // 校验msg
        if (request == null || !StringUtils.hasText(request.getType())){
            return;
        }
        // 连接相关
        if (request.getType().contains(RequestMessageType.Connect.root)){
            String userAccount = request.getSenderId();
            if (!StringUtils.hasText(userAccount)){
                return;
            }
            // 注册
            if (request.getType().contains(RequestMessageType.Connect.CONNECT)){
                channelManager.register(userAccount, channel);
            }
            // 断开连接
            else if (request.getType().contains(RequestMessageType.Connect.DISCONNECT)){
                channelManager.unRegister(userAccount);
            }
        }
        // RemoteEvent发送给其他实例
        if (channel.isActive()){
            // ToService
            if (request.getType().contains(RequestMessageType.ToServer.root)){
                // ping
                if (request.getType().contains(RequestMessageType.ToServer.PING)){
                    // ping -> pong
                    Message pong = messageConverter.requestBodyToMessage(request);
                    String pongType = MessageTypeTranslator.translate(request.getType());
                    pong.setType(pongType);
                    pong.setReceiverId(request.getSenderId());
                    pong.setSenderId(Constants.SERVER_ID);

                    rabbitMqSender.sendToMessageService(pong);
                    return;
                }
            }
            // 其他
            Message message = messageConverter.requestBodyToMessage(request);
            rabbitMqSender.sendToMessageService(message);
        }
    }

}
