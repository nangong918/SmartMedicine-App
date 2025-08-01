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
    // 消息资源 （资源的idStr，不是user头像）
    public String fileIdStr = null;

    public void setData(UserChatLastMessageBo bo){
        super.setData(bo);
        this.unreadCount = bo.unreadCount;
        if (bo.fileIdStr != null){
            this.fileIdStr = bo.fileIdStr;
        }
    }
}
