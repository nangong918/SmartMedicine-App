package com.czy.api.api.relationship;

import com.czy.api.domain.ao.relationship.AddUserAo;
import com.czy.api.domain.ao.relationship.HandleAddedMeAo;
import com.czy.api.domain.ao.relationship.MyFriendItemAo;
import com.czy.api.domain.ao.relationship.NewUserItemAo;
import com.czy.api.domain.ao.relationship.SearchFriendApplyAo;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.UserViewEntity;


import java.util.List;

/**
 * @author 13225
 * @date 2025/3/31 11:19
 */
public interface UserRelationshipService {

    /**
     * 添加好友:消息推送用的netty；此方法用于持久化
     * @param addUserAo
     * @return
     */
    boolean addUserFriend(AddUserAo addUserAo);

    // 取消添加好友
    @Deprecated
    boolean cancelAddUserFriend(AddUserAo addUserAo);

    /**
     * 处理加好友请求
     * @param handleAddedMeAo
     */
    Message handleAddedUser(HandleAddedMeAo handleAddedMeAo);

    Message sendHandleResultToApplier(HandleAddedMeAo handleAddedMeAo, String responseType);

    /**
     * 获取好友列表
     * @param senderAccount
     * @return
     */
    List<UserViewEntity> getFriendList(String senderAccount);

    // 获取我被别人加的好友请求列表
    List<NewUserItemAo> getAddMeRequestList(String handlerAccount);

    // 获取我加别人的好友列表
    List<NewUserItemAo> getHandleMyAddUserResponseList(String senderAccount);

    /**
     * 获取我加别人的好友列表
     * @param senderAccount
     * @return
     */
    List<MyFriendItemAo> getMyFriendList(String senderAccount);

    // 搜索好友及其添加状态
    List<SearchFriendApplyAo> searchFriend(String applyAccount, String handlerAccount);

    void updateApplyStatus(AddUserAo addUserAo);

    void deleteApplyStatus(AddUserAo addUserAo);

    void deleteFriend(AddUserAo addUserAo);
}
