package com.czy.api.converter.base;

import cn.hutool.core.bean.BeanUtil;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.model.ResponseBodyProto;
import org.mapstruct.Mapper;

import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/3/31 17:22
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BaseResponseConverter {

    BaseResponseConverter INSTANCE = Mappers.getMapper(BaseResponseConverter.class);

    // BaseResponseData -> ResponseBodyProto.ResponseBody
    default ResponseBodyProto.ResponseBody getResponseBody(BaseResponseData baseResponseData) {
        if (baseResponseData == null){
            return null;
        }
        Map<String, String> dataMapStr = dataMap(baseResponseData);
        return ResponseBodyProto.ResponseBody.newBuilder()
                .setSenderId(baseResponseData.getSenderId())
                .setReceiverId(baseResponseData.getReceiverId())
                .setType(baseResponseData.getType())
                .setTimestamp(Long.parseLong(baseResponseData.getTimestamp()))
                .putAllData(dataMapStr)
                .setCode(baseResponseData.getCode())
                .setMessage(baseResponseData.getMessage())
                .build();
    }

    // Message -> ResponseBodyProto.ResponseBody
    default ResponseBodyProto.ResponseBody getResponseBody(Message message) {
        if (message == null){
            return null;
        }
        String code = Optional.ofNullable(message.getData())
                .map(dataMap -> dataMap.get("code"))
                .orElse("");
        String msg = Optional.ofNullable(message.getData())
                .map(dataMap -> dataMap.get("message"))
                .orElse("");
        return ResponseBodyProto.ResponseBody.newBuilder()
                .setCode(code)
                .setMessage(msg)
                .setSenderId(message.getSenderId())
                .setReceiverId(message.getReceiverId())
                .setType(message.getType())
                .setTimestamp(message.getTimestamp())
                .putAllData(message.getData())
                .build();
    }

    // BaseResponseData -> Message
    default Message getMessage(BaseResponseData responseData) {
        Message message = new Message();

        // 只需调用原来的映射逻辑
        message.setSenderId(responseData.getSenderId());
        message.setReceiverId(responseData.getReceiverId());
        message.setType(responseData.getType());
        message.setTimestamp(Long.valueOf(responseData.getTimestamp()));

        // 这里构建 data Map，并放入 code 和 message
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("code", responseData.getCode()); // 假设 BaseResponseData 有 getCode() 方法
        dataMap.put("message", responseData.getMessage()); // 假设 BaseResponseData 有 getMessage() 方法

        // 设置 data 到 Message
        message.setData(dataMap);

        return message;
    }

    // BaseResponseData -> Map
    default Map<String, String> dataMap(BaseResponseData responseData) {
        Map<String, Object> dataMap = BeanUtil.beanToMap(responseData);
        return dataMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Optional.ofNullable(entry.getValue()).map(Object::toString).orElse("")
                ));
    }

}
