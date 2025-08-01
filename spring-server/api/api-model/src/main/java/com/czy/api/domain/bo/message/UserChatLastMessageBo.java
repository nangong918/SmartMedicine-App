package com.czy.api.domain.bo.message;

import com.czy.api.domain.entity.FriendViewEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author 13225
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserChatLastMessageBo extends UserChatMessageBo{
    public Integer unreadCount = 0;
    // 虽然说存在sender和receiver，但是我可能是sender也可能是receiver。所以对方需要记录
    public FriendViewEntity friendViewEntity;
}
