package com.czy.api.converter.domain.relationship;

import com.czy.api.domain.ao.oss.FileResAo;
import com.czy.api.domain.ao.relationship.AddUserStatusAo;
import com.czy.api.domain.ao.relationship.SearchFriendApplyAo;
import com.czy.api.domain.bo.relationship.SearchFriendApplyBo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/29 11:28
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SearchFriendApplyConverter {
    // INSTANCE
    SearchFriendApplyConverter INSTANCE = Mappers.getMapper(SearchFriendApplyConverter.class);

    // bo -> ao
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "applyTime", source = "applyTime")
    @Mapping(target = "handleTime", source = "handleTime")
    @Mapping(target = "source", source = "source")
    @Mapping(target = "chatList", source = "chatList")
//    @Mapping(target = "avatarFileId", source = "avatarFileId")
    SearchFriendApplyAo boToAo_(SearchFriendApplyBo bo);

    default SearchFriendApplyAo boToAo(SearchFriendApplyBo bo){
        SearchFriendApplyAo ao = boToAo_(bo);
        AddUserStatusAo addUserStatusAo = new AddUserStatusAo();
        addUserStatusAo.setApplyStatus(bo.getApplyStatus());
        addUserStatusAo.setHandleStatus(bo.getHandleStatus());
        addUserStatusAo.setBlack(bo.isBlack());
        addUserStatusAo.setApplyAccount(bo.getApplyAccount());
        addUserStatusAo.setHandlerAccount(bo.getHandlerAccount());
        ao.setAddUserStatusAo(addUserStatusAo);
        // file
        FileResAo fileResAo = new FileResAo();
        fileResAo.setFileId(bo.getAvatarFileId());
        fileResAo.setFileUrl(bo.getAvatarUrl());
        ao.setFileResAo(fileResAo);
        return ao;
    };
}
