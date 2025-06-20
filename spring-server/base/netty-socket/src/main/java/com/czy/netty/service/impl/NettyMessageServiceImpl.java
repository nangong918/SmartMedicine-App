package com.czy.netty.service.impl;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.entity.event.Message;
import com.czy.netty.service.NettyMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/6/19 11:25
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NettyMessageServiceImpl implements NettyMessageService {

    /**
     * 判断netty的message应该选择哪个mq传输给service
     * @param message   netty发送给socket的消息
     * @return mq类型
     * 普配依据：
     * @see com.czy.api.constant.netty.RequestMessageType
     * 目标类型：
     * @see com.czy.api.constant.netty.MqConstants
     */
    @Override
    public String getNettyMessageMq(Message message){
        String type = message.getType();
        if (type.contains(RequestMessageType.Chat.root) || type.contains(RequestMessageType.Call.root)){
            return MqConstants.MessageQueue.ID;
        }
        else if (type.contains(RequestMessageType.Friend.root)){
            return MqConstants.RelationshipQueue.ID;
        }
        else if (type.contains(RequestMessageType.Post.root)){
            return MqConstants.PostQueue.ID;
        }
        else if (type.contains(RequestMessageType.Oss.root)){
            return MqConstants.OssQueue.ID;
        }

        else if (type.contains(RequestMessageType.Connect.root)
         || type.contains(RequestMessageType.ToServer.root)){
            return MqConstants.TO_SERVICE;
        }

        return null;
    }

}
