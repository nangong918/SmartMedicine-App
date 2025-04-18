package com.czy.api.converter.mongoEs;

import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.message.UserChatMessageEsDo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/17 11:35
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserChatMessageEsConverter {
    UserChatMessageEsConverter INSTANCE = Mappers.getMapper(UserChatMessageEsConverter.class);

    // mongo -> es
    @Mapping(target = "id", source = "id")
    @Mapping(target = "msgContent", source = "msgContent")
    @Mapping(target = "msgType", source = "msgType")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "receiverId", source = "receiverId")
    @Mapping(target = "timestamp", source = "timestamp")
    UserChatMessageEsDo mongoToEsDo(UserChatMessageDo userChatMessageDo);

    // es -> mongo
    @Mapping(target = "id", source = "id")
    @Mapping(target = "msgContent", source = "msgContent")
    @Mapping(target = "msgType", source = "msgType")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "receiverId", source = "receiverId")
    @Mapping(target = "timestamp", source = "timestamp")
    UserChatMessageDo esToMongoDo(UserChatMessageEsDo userChatMessageEsDo);

    // mongoList -> esList
    default List<UserChatMessageEsDo> mongoListToEsList(List<UserChatMessageDo> userChatMessageDoList) {
        return userChatMessageDoList.stream().map(this::mongoToEsDo).collect(Collectors.toList());
    }

    // esList -> mongoList
    default List<UserChatMessageDo> esListToMongoList(List<UserChatMessageEsDo> userChatMessageEsDoList) {
        return userChatMessageEsDoList.stream().map(this::esToMongoDo).collect(Collectors.toList());
    }
}
