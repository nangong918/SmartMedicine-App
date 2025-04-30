package com.czy.api.converter.domain.relationship;

import com.czy.api.domain.ao.relationship.AddUserStatusAo;
import com.czy.api.domain.ao.relationship.NewUserItemAo;
import com.czy.api.domain.bo.relationship.NewUserItemBo;
import com.czy.api.domain.entity.UserViewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/29 13:55
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewUserItemConverter {
    // INSTANCE
    NewUserItemConverter INSTANCE = Mappers.getMapper(NewUserItemConverter.class);

    // bo -> ao
    @Mapping(target = "applyTime", source = "applyTime")
    @Mapping(target = "handleTime", source = "handleTime")
    @Mapping(target = "addSource", source = "addSource")
    NewUserItemAo boToAo_(NewUserItemBo bo);

    default NewUserItemAo boToAo(NewUserItemBo bo) {
        NewUserItemAo ao = boToAo_(bo);
        // 设置用户状态
        ao.setAddUserStatusAo(boToAddUserStatusAo(bo));
        // 设置用户view信息
        UserViewEntity userViewEntity = boToUserViewEntity(bo);
        ao.setUserViewEntity(userViewEntity);
        // 计算并设置用户的状态是否是被添加
        ao.setBeAdd(ao.getAddUserStatusAo().isBeAdd(bo.getApplyAccount()));
        return ao;
    }

    @Mapping(target = "applyStatus", source = "applyStatus")
    @Mapping(target = "handleStatus", source = "handleStatus")
    @Mapping(target = "applyAccount", source = "applyAccount")
    @Mapping(target = "handlerAccount", source = "handlerAccount")
    AddUserStatusAo boToAddUserStatusAo_(NewUserItemBo bo);

    default AddUserStatusAo boToAddUserStatusAo(NewUserItemBo bo){
        AddUserStatusAo ao = boToAddUserStatusAo_(bo);
        ao.setBlack(bo.getIsBlack() != 0);
        return ao;
    }

    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "userAccount", source = "userAccount")
    @Mapping(target = "avatarFileId", source = "avatarFileId")
    UserViewEntity boToUserViewEntity(NewUserItemBo bo);
}
