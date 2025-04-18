package com.czy.api.converter.socket.request;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.domain.dto.socket.request.HaveReadMessageRequest;
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
public interface HaveReadMessageReqConverter {

    HaveReadMessageReqConverter INSTANCE = Mappers.getMapper(HaveReadMessageReqConverter.class);

    // 耗时4ms
    // ProtoBufRequest.dataMap -> HaveReadMessageRequest
    default HaveReadMessageRequest getRequest(RequestBodyProto.RequestBody requestBody) {
        HaveReadMessageRequest request = new HaveReadMessageRequest();
        request.setBaseRequestData(BaseRequestConverter.INSTANCE.getBaseRequestData(requestBody));
        Map<String, String> data = requestBody.getDataMap();
        request.setReceiverUserAccount(data.get("receiverUserAccount"));
        return request;
    }
}
