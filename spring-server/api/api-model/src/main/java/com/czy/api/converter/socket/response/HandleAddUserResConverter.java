package com.czy.api.converter.socket.response;


import com.czy.api.domain.dto.socket.response.HandleAddUserResponse;
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
public interface HandleAddUserResConverter {

    HandleAddUserResConverter INSTANCE = Mappers.getMapper(HandleAddUserResConverter.class);

    // 耗时：7ms
    // Response -> Message
    default Message getMessage(HandleAddUserResponse response) {
        Message message = response.getMessageByResponse();
        if (message == null) {
            message = new Message();
        }
        Map<String, String> data = new HashMap<>();
        data.put("applyStatus", String.valueOf(response.getApplyStatus()));
        data.put("handleStatus", String.valueOf(response.getHandleStatus()));
        data.put("isBlack", String.valueOf(response.isBlack()));
        data.put("applyAccount", response.getApplyAccount());
        data.put("handlerAccount", response.getHandlerAccount());
        data.put("additionalContent", response.getAdditionalContent());
        data.put("userAccount", response.getUserAccount());
        data.put("userName", response.getUserName());
        data.put("avatarUrl", response.getAvatarUrl());
        message.setData(data);
        return message;
    }
}
