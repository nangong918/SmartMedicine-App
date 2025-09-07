package com.czy.domain.dto.netty.base;


/**
 * @author 13225
 * @date 2025/7/28 11:51
 */
public class PostOperationBaseResponse extends NettyOptionResponse {
    // 帖子id
    public Long postId;
    // 发送者id
    public Long senderId;
    // 接收者id
    public Long receiverId;
}
