package com.czy.api.converter.domain.user;

import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.entity.UserViewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/6/9 11:31
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserConverter {

    // INSTANCE
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    // LoginUserDo -> UserDo
    default UserDo toUserDo_(LoginUserDo loginUserDo){
        UserDo userDo = new UserDo();
        userDo.setId(loginUserDo.getId());
        userDo.setUserName(loginUserDo.getUserName());
        userDo.setAccount(loginUserDo.getAccount());
        userDo.setAccount(loginUserDo.getAccount());
        return userDo;
    };

    // LoginUserDo -> UserFeatureNeo4jDo
    default UserFeatureNeo4jDo toUserFeatureNeo4jDo_(LoginUserDo loginUserDo){
        UserFeatureNeo4jDo userFeatureNeo4jDo = new UserFeatureNeo4jDo();
        userFeatureNeo4jDo.setUserId(loginUserDo.getId());
        // 注意此处是neo4j设计的bug，为了方便查找的属性，每个baseNeo4j实体都需要标注自己的唯一name，这个name是userAccount而不是UserName，因为UserName不唯一
        userFeatureNeo4jDo.setName(loginUserDo.getAccount());
        userFeatureNeo4jDo.setAccount(loginUserDo.getAccount());
        return userFeatureNeo4jDo;
    };

    // userDo -> userViewEntity
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "userAccount", source = "account")
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "avatarFileId", source = "avatarFileId")
    UserViewEntity toUserViewEntity_(UserDo userDo);

    default UserViewEntity toUserViewEntity(UserDo userDo, String avatarUrl){
        UserViewEntity userViewEntity = toUserViewEntity_(userDo);
        userViewEntity.setAvatarUrl(avatarUrl);
        return userViewEntity;
    }
}
