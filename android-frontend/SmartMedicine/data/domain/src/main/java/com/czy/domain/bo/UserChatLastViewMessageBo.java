package com.czy.domain.bo;


import com.czy.domain.vo.entity.FriendViewEntity;

/**
 * @author 13225
 * @date 2025/8/1 10:40
 */
public class UserChatLastViewMessageBo extends UserChatLastMessageBo{
    // 虽然说存在sender和receiver，但是我可能是sender也可能是receiver。所以对方需要记录
    public FriendViewEntity friendViewEntity;

    public void setData(FriendViewEntity friendViewEntity, UserChatLastMessageBo bo){
        this.friendViewEntity = friendViewEntity;
        super.setData(bo);
    }
}
