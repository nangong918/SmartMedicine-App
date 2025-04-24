package com.czy.api.converter.domain.netty;

import com.czy.api.domain.dto.http.request.RegisterRequest;
import com.czy.api.domain.entity.event.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/1 17:43
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SessionConverter {

    SessionConverter INSTANCE = Mappers.getMapper(SessionConverter.class);

    @Mapping(source = "uuid", target = "uuid")
    @Mapping(source = "deviceId", target = "deviceId")
    @Mapping(source = "deviceName", target = "deviceName")
    @Mapping(source = "appVersion", target = "appVersion")
    @Mapping(source = "osVersion", target = "osVersion")
    @Mapping(source = "packageName", target = "packageName")
    @Mapping(source = "language", target = "language")
    @Mapping(target = "uid", source = "senderId")
    @Mapping(target = "type", source = "type")
    Session getSession(RegisterRequest request);
}
