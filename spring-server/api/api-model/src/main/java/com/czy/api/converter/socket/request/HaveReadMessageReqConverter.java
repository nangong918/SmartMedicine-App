package com.czy.api.converter.socket.request;

import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.domain.dto.socket.request.HaveReadMessageRequest;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.Optional;

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
        request.setReceiverUserId(
                Optional.ofNullable(data.get("receiverUserId"))
                        .map(idSre -> {
                            try {
                                return Long.parseLong(idSre);
                            } catch (Exception e){
                                return NettyConstants.ERROR_ID;
                            }
                        })
                        .orElse(NettyConstants.ERROR_ID)
        );
        return request;
    }
}
