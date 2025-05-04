package com.czy.api.converter.domain.relationship;

import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.domain.ao.relationship.AddUserAo;
import com.czy.api.domain.dto.http.response.AddUserToTargetUserResponse;
import com.czy.api.domain.dto.socket.request.AddUserRequest;
import com.czy.api.domain.dto.socket.request.DeleteUserRequest;
import com.czy.api.domain.dto.socket.response.DeleteUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/30 14:37
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddUserConverter {
    AddUserConverter INSTANCE = Mappers.getMapper(AddUserConverter.class);

    // request -> ao
    @Mapping(target = "applyAccount", source = "senderId")
    @Mapping(target = "handlerAccount", source = "receiverId")
    @Mapping(target = "applyContent", constant = "addContent")
    @Mapping(target = "source", source = "source")
    @Mapping(target = "applyStatus", source = "applyType")
    AddUserAo requestToAo_(AddUserRequest request);

    default AddUserAo requestToAo(AddUserRequest request){
        AddUserAo ao = requestToAo_(request);
        ao.setApplyTime(Long.valueOf(request.getTimestamp()));
        return ao;
    }

    // request -> response
    // 交换收发者
    @Mapping(target = "receiverId", source = "senderId")
    @Mapping(target = "senderId", source = "receiverId")
    @Mapping(target = "appliedUserName", source = "myName")
    @Mapping(target = "appliedUserAddContent", source = "addContent")
    @Mapping(target = "appliedUserApplyStatus", source = "applyType")
    @Mapping(target = "appliedUserSource", source = "source")
    AddUserToTargetUserResponse requestToResponse_(AddUserRequest request);

    default AddUserToTargetUserResponse requestToResponse(AddUserRequest request){
        AddUserToTargetUserResponse response = requestToResponse_(request);
        response.setAppliedUserAddTime(Long.valueOf(request.getTimestamp()));
        response.setType(MessageTypeTranslator.translateClean(request.getType()));
        return response;
    }

    // deleteRequest -> ao
    @Mapping(target = "applyAccount", source = "senderId")
    @Mapping(target = "handlerAccount", source = "receiverId")
    @Mapping(target = "applyStatus", ignore = true)
    AddUserAo deleteRequestToAo_(DeleteUserRequest request);

    default AddUserAo deleteRequestToAo(DeleteUserRequest request){
        AddUserAo ao = deleteRequestToAo_(request);
        ao.setApplyTime(Long.valueOf(request.getTimestamp()));
        return ao;
    }

    // delete request -> delete response
    // 交换收发id
    @Mapping(target = "receiverId", source = "senderId")
    @Mapping(target = "senderId", source = "receiverId")
    DeleteUserResponse deleteRequestToDeleteResponse_(DeleteUserRequest request);

    default DeleteUserResponse deleteRequestToDeleteResponse(DeleteUserRequest request){
        DeleteUserResponse response = deleteRequestToDeleteResponse_(request);
        response.setType(MessageTypeTranslator.translateClean(request.getType()));
        return response;
    }

}
