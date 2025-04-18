package com.czy.api.converter.socket.response;


import com.czy.api.domain.dto.socket.response.HaveReadMessageResponse;
import com.czy.api.domain.entity.event.Message;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/10 11:20
 * Request -> Response(InitByRequest)
 * Response -> Message
 * Message -> Protobuf.Response
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HaveReadMessageResConverter {

    HaveReadMessageResConverter INSTANCE = Mappers.getMapper(HaveReadMessageResConverter.class);

    // 耗时：7ms
    // Response -> Message
    default Message getMessage(HaveReadMessageResponse response) {
        Message message = response.getMessageByResponse();
        if (message == null) {
            message = new Message();
        }
        Map<String, String> data = new HashMap<>();
        data.put("receiverAccount", response.getReceiverAccount());
        message.setData(data);
        return message;
    }
}
