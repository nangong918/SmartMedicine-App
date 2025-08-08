package com.czy.api.domain.dto.http.response;


import com.czy.api.domain.bo.message.UserChatLastViewMessageBo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/26 14:30
 */
@Data
public class UserNewMessageResponse {
    public List<UserChatLastViewMessageBo> lastMessageList;
}
