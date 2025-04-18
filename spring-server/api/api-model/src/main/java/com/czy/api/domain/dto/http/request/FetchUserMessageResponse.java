package com.czy.api.domain.dto.http.request;


import com.czy.api.domain.bo.message.UserChatMessageBo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/26 14:53
 */
@Data
public class FetchUserMessageResponse {
    public List<UserChatMessageBo> messageList;
}
