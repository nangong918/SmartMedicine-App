package com.czy.api.converter.base;


import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.model.RequestBodyProto;
import com.czy.api.domain.entity.model.ResponseBodyProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;

/**
 * @author 13225
 * @date 2025/3/29 9:57
 * 禁止使用hutool的BeanUtil.copyProperties和BeanUtil.beanToMap进行对象转换
 * 因为IM系统中消息接收非常频繁，类型转换应该用MapStruct进行静态类型转换，而不是使用hutool的动态反射进行转换。
 * 并且Protobuf本身解决的问题就是JSON的String用反射转换为对应的对象。反射解析非常慢。所以应该使用Protobuf + MapStruct静态解析避免使用反射。
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageConverter {
    MessageConverter INSTANCE = Mappers.getMapper(MessageConverter.class);

    // 耗时：3ms
    // 将 RequestBodyProto.RequestBody 转换为 Message
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "receiverId", source = "receiverId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "data", source = "dataMap")
    @Mapping(target = "timestamp", source = "timestamp")
    Message requestBodyToMessage(RequestBodyProto.RequestBody requestBody);

    // 耗时：4ms
    // 将 ResponseBodyProto.ResponseBody 转换为 Message
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "receiverId", source = "receiverId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "data", source = "dataMap")
    @Mapping(target = "timestamp", source = "timestamp")
    Message responseBodyToMessage(ResponseBodyProto.ResponseBody responseBody);

    // 耗时：99ms (builder.build()时间比较长)
    // 将 Message 转换为 ResponseBodyProto.ResponseBody
    default ResponseBodyProto.ResponseBody messageToResponseBody(Message message) {
        // 从 Message 转换为 ResponseBodyProto.ResponseBody.Builder
        ResponseBodyProto.ResponseBody.Builder builder = ResponseBodyProto.ResponseBody.newBuilder();
        builder.setCode(String.valueOf(HttpStatus.OK));
        builder.setSenderId(message.getSenderId());
        builder.setReceiverId(message.getReceiverId());
        builder.setType(message.getType());
        builder.setTimestamp(message.getTimestamp());
        builder.putAllData(message.getData());
        return builder.build(); // 返回构建好的 ResponseBody
    }
}
