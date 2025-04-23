package com.czy.post.handler.api;

import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.socket.request.PostCollectRequest;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import com.czy.api.domain.dto.socket.request.PostForwardRequest;
import com.czy.api.domain.dto.socket.request.PostLikeRequest;
import com.czy.springUtils.annotation.MessageType;

/**
 * @author 13225
 * @date 2025/4/23 14:54
 */
public interface PostApi {

    @MessageType(value = RequestMessageType.Post.COLLECT_POST, desc = "收藏帖子")
    void postCollect(PostCollectRequest request);

    @MessageType(value = RequestMessageType.Post.COMMENT_POST, desc = "评论帖子")
    void postComment(PostCommentRequest request);

    @MessageType(value = RequestMessageType.Post.FORWARD_POST, desc = "转发帖子")
    void postForward(PostForwardRequest request);

    @MessageType(value = RequestMessageType.Post.LIKE_POST, desc = "点赞帖子")
    void postLike(PostLikeRequest request);
}
