package com.czy.netty.component;

import com.czy.api.converter.base.BaseResponseConverter;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.model.ResponseBodyProto;
import com.czy.netty.channel.ChannelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 13225
 * @date 2025/4/1 16:17
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class ToClientMessageSender {

    private final BaseResponseConverter baseResponseConverter;
    private final ChannelManager channelManager;

    /**
     * 推送消息给客户端
     * @param message   消息
     */
    public void pushToClient(Message message) {
        if (message == null || StringUtils.isEmpty(message.getReceiverId())){
            return;
        }
        ResponseBodyProto.ResponseBody responseBody = baseResponseConverter.getResponseBody(message);
        channelManager.pushToClient(
                message.getReceiverId(),
                responseBody
        );
    }

}
