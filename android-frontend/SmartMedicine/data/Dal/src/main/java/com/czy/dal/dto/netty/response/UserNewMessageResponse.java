package com.czy.dal.dto.netty.response;


import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.bo.UserChatLastViewMessageBo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/26 14:30
 */
public class UserNewMessageResponse implements BaseBean {
    public List<UserChatLastViewMessageBo> lastMessageList;
}
