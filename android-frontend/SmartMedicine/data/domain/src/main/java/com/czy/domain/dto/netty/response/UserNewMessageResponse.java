package com.czy.domain.dto.netty.response;


import com.czy.baseUtilLib.json.BaseBean;
import com.czy.domain.bo.UserChatLastViewMessageBo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/26 14:30
 */
public class UserNewMessageResponse implements BaseBean {
    public List<UserChatLastViewMessageBo> lastMessageList;
}
