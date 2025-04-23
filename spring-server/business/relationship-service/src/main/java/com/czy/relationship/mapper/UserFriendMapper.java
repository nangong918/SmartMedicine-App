package com.czy.relationship.mapper;



import com.czy.api.domain.Do.relationship.UserFriendDo;
import com.czy.api.domain.entity.UserViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/20 17:37
 */
@Mapper
public interface UserFriendMapper {

    // 添加好友关系
    int addUserFriend(UserFriendDo userFriendDo);

    // 查询好友关系
    UserFriendDo getUserFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // 更新好友关系 (保险方法，因为user_id后续可能会变)
    int updateUserFriend(UserFriendDo userFriendDo);

    // 删除好友关系
    int deleteUserFriend(UserFriendDo userFriendDo);

    // 查询用户的全部好友
    List<UserFriendDo> getUserFriends(int userId);

    // 通过userAccount查询用户好友的全部好友及其view信息
    List<UserViewEntity> getUserFriendsViewByAccount(String userAccount);

    // 通过userId查询用户好友的全部好友及其view信息
    List<UserViewEntity> getUserFriendsViewByUserId(int userId);

    // 查询用户的好友数量
    int getUserFriendsCount(int userId);

    // 查询两个用户是否是好友
    int isFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

}
