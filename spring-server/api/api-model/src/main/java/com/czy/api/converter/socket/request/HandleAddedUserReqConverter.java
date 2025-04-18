package com.czy.api.converter.socket.request;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.domain.dto.socket.request.HandleAddedUserRequest;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/10 11:20
 * 方法废弃，直接用RequestBodyProto.RequestBody交给Handler层，然后手动读取。
 * IM系统中面向对象的封装是很浪费时间的
 * ProtoBufRequest -> BaseRequestData
 * ProtoBufRequest.dataMap -> AddUserReqConverter
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HandleAddedUserReqConverter {

    HandleAddedUserReqConverter INSTANCE = Mappers.getMapper(HandleAddedUserReqConverter.class);

    // 耗时4ms
    // ProtoBufRequest.dataMap -> HandleAddedUserRequest
    default HandleAddedUserRequest getRequest(RequestBodyProto.RequestBody requestBody) {
        HandleAddedUserRequest request = new HandleAddedUserRequest();
        request.setBaseRequestData(BaseRequestConverter.INSTANCE.getBaseRequestData(requestBody));
        Map<String, String> data = requestBody.getDataMap();
        request.setHandleType(Integer.parseInt(data.get("handleType")));
        request.setAdditionalContent(data.get("additionalContent"));
        return request;
    }
}
