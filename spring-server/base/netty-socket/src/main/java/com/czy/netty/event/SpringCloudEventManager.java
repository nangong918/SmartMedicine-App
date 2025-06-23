package com.czy.netty.event;


import com.czy.api.constant.netty.KafkaConstant;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.converter.base.MessageConverter;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.model.RequestBodyProto;
import com.czy.netty.channel.ChannelManager;
//import com.czy.netty.mq.sender.RabbitMqSender;
import com.czy.netty.component.ToClientMessageSender;
import com.czy.netty.kafka.KafkaSender;
import com.czy.netty.mq.sender.ToServiceMqSender;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/3/28 18:25
 * 微服务事件管理者
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SpringCloudEventManager {

//    private final RabbitMqSender rabbitMqSender;
    private final ToServiceMqSender toServiceMqSender;
    private final MessageConverter messageConverter;
    private final ChannelManager channelManager;
    private final ToClientMessageSender toClientMessageSender;
    private final KafkaSender kafkaSender;

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
            Long senderId = request.getSenderId();
            // 注册
            if (request.getType().contains(RequestMessageType.Connect.CONNECT)){
                channelManager.register(senderId, channel);
            }
            // 断开连接
            else if (request.getType().contains(RequestMessageType.Connect.DISCONNECT)){
                channelManager.unRegister(senderId);
            }
        }

        // 消息广播
        if (channel.isActive()){
            // 发送给前端的消息
            // ToService
            if (request.getType().contains(RequestMessageType.ToServer.root)){
                // ping
                if (request.getType().contains(RequestMessageType.ToServer.PING)){
                    // ping -> pong
                    Message pong = messageConverter.requestBodyToMessage(request);
                    String pongType = MessageTypeTranslator.translate(request.getType());
                    pong.setType(pongType);
                    pong.setReceiverId(request.getSenderId());
                    pong.setSenderId(NettyConstants.SERVER_ID);

                    // 发送给前端
                    toClientMessageSender.pushToClient(pong);
                    return;
                }
            }

            // 日志消息相关 -> 交给kafka
            if (request.getType().contains(RequestMessageType.Logging.root)){
                Message message = messageConverter.requestBodyToMessage(request);
                if (message.getData() != null){
                    message.getData().put(KafkaConstant.KAFKA_TOPIC, KafkaConstant.Topic.Point);
                }
                else {
                    Map<String, String> map = new HashMap<>();
                    map.put(KafkaConstant.KAFKA_TOPIC, KafkaConstant.Topic.Point);
                    message.setData(map);
                }
                kafkaSender.send(message);
            }
            // 帖子相关
            else if (request.getType().contains(RequestMessageType.Post.root)){
                Message message = messageConverter.requestBodyToMessage(request);
                if (message.getData() != null){
                    message.getData().put(KafkaConstant.KAFKA_TOPIC, KafkaConstant.Topic.Post_Operation);
                }
                else {
                    Map<String, String> map = new HashMap<>();
                    map.put(KafkaConstant.KAFKA_TOPIC, KafkaConstant.Topic.Post_Operation);
                    message.setData(map);
                }
                kafkaSender.send(message);
            }

            // 其他：分类mq发送微服务的消息
            Message message = messageConverter.requestBodyToMessage(request);
            toServiceMqSender.sendToService(message);
        }
    }

}
