package com.czy.api.converter.domain.relationship;

import com.czy.api.domain.ao.relationship.HandleAddedMeAo;
import com.czy.api.domain.dto.socket.request.HandleAddedUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/30 15:05
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HandleAddUserConverter {
    HandleAddUserConverter INSTANCE = Mappers.getMapper(HandleAddUserConverter.class);

    // request -> ao
    @Mapping(target = "handleType", source = "handleType")
    // 接收方是申请人
    @Mapping(target = "applyAccount", source = "receiverId")
    // 请求方是处理人
    @Mapping(target = "handlerAccount", source = "senderId")
    @Mapping(target = "additionalContent", source = "additionalContent")
    HandleAddedMeAo requestToAo_(HandleAddedUserRequest request);

    default HandleAddedMeAo requestToAo(HandleAddedUserRequest request) {
        HandleAddedMeAo ao = requestToAo_(request);
        ao.setHandleTime(Long.valueOf(request.getTimestamp()));
        return ao;
    }
}
