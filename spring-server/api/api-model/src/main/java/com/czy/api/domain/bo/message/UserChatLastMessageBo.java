package com.czy.api.domain.bo.message;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author 13225
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserChatLastMessageBo extends UserChatMessageBo{
    public Integer unreadCount = 0;
}
