package com.czy.api.converter.base;

import cn.hutool.core.bean.BeanUtil;
import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/3/31 17:04
 * TODO 为每个类型都写一个静态的MapStruct
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BaseRequestConverter {
    BaseRequestConverter INSTANCE = Mappers.getMapper(BaseRequestConverter.class);

    // ProtoBufRequest -> BaseRequest
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "receiverId", source = "receiverId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "timestamp", source = "timestamp")
    BaseRequestData getBaseRequestData(RequestBodyProto.RequestBody requestBody);

    // 耗时:90ms
    // 暂时不删除，给EventManager留作反射方案用于测试用
    // 将 Message 转换为 BaseRequestData
    @Deprecated
    default <T extends BaseRequestData> T getBaseRequestData(Message message, Class<T> tClazz) {
        if (message == null || tClazz == null){
            return null;
        }
        T t = null;
        try {
            t = tClazz.getDeclaredConstructor().newInstance();
            Map<String, String> dataMap = message.getData();
            BeanUtil.copyProperties(dataMap, t);
            t.setSenderId(message.getSenderId());
            t.setReceiverId(message.getReceiverId());
            t.setType(message.getType());
            t.setTimestamp(String.valueOf(message.getTimestamp()));
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    // 耗时:110ms
    // 自定义将 BaseRequestData 转换为 Map
    default Map<String, String> dataMap(BaseRequestData baseRequestData) {
        Map<String, Object> dataMap = BeanUtil.beanToMap(baseRequestData);
        return dataMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Optional.ofNullable(entry.getValue()).map(Object::toString).orElse("")
                ));
    }

    // 自定义转换 Long
    default long stringToLong(String timestamp) {
        return Long.parseLong(timestamp);
    }
}
