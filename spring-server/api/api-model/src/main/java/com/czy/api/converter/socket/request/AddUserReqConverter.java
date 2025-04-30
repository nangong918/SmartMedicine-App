package com.czy.api.converter.socket.request;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.domain.dto.socket.request.AddUserRequest;
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
public interface AddUserReqConverter {

    AddUserReqConverter INSTANCE = Mappers.getMapper(AddUserReqConverter.class);

    // 耗时4ms
    // ProtoBufRequest.dataMap -> AddUserReqConverter
    default AddUserRequest getRequest(RequestBodyProto.RequestBody requestBody) {
        AddUserRequest request = new AddUserRequest();
        request.setBaseRequestData(BaseRequestConverter.INSTANCE.getBaseRequestData(requestBody));
        Map<String, String> data = requestBody.getDataMap();
        request.setSenderId(requestBody.getSenderId());
        request.setReceiverId(requestBody.getReceiverId());
        request.setMyName(data.get("myName"));
        request.setAddContent(data.get("addContent"));
        request.setSource(Integer.parseInt(data.get("source")));
        request.setApplyType(Integer.parseInt(data.get("applyType")));
        return request;
    }
}
