package com.czy.api.domain.dto.socket.response;

import com.czy.api.domain.dto.base.NettyOptionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/7/28 11:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostOperationBaseResponse extends NettyOptionResponse {
    // 帖子id
    public Long postId;
    // 发送者id
    public Long senderId;
    // 接收者id
    public Long receiverId;

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("postId", String.valueOf(postId));
        map.put("senderId", String.valueOf(senderId));
        map.put("receiverId", String.valueOf(receiverId));
        return map;
    }
}
