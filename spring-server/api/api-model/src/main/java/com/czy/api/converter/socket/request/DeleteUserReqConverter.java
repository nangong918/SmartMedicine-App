package com.czy.api.converter.socket.request;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.domain.dto.socket.request.DeleteUserRequest;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/10 11:20
 * 方法废弃，直接用RequestBodyProto.RequestBody交给Handler层，然后手动读取。
 * IM系统中面向对象的封装是很浪费时间的
 * ProtoBufRequest -> BaseRequestData
 * ProtoBufRequest.dataMap -> AddUserReqConverter
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeleteUserReqConverter {

    DeleteUserReqConverter INSTANCE = Mappers.getMapper(DeleteUserReqConverter.class);

    // 耗时
    // ProtoBufRequest.dataMap -> DeleteUserRequest
    default DeleteUserRequest getRequest(RequestBodyProto.RequestBody requestBody) {
        DeleteUserRequest request = new DeleteUserRequest();
        request.setBaseRequestData(BaseRequestConverter.INSTANCE.getBaseRequestData(requestBody));
        // 因为删除的类型必然是final，所以不用
//        Map<String, String> data = requestBody.getDataMap();
//        request.setApplyType(Integer.parseInt(data.get("applyType")));
        return request;
    }
}
