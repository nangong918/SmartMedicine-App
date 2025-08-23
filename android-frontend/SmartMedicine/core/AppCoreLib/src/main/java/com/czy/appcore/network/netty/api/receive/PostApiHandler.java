package com.czy.appcore.network.netty.api.receive;

import androidx.annotation.NonNull;

import com.czy.domain.annotation.MessageType;
import com.czy.domain.constant.netty.ResponseMessageType;
import com.czy.domain.dto.netty.response.PostCommentResponse;
import com.czy.domain.dto.netty.response.PostForwardResponse;
import com.czy.domain.dto.netty.response.PostLikeResponse;

public interface PostApiHandler {
    @MessageType(value = ResponseMessageType.Post.LIKE_POST, desc = "post被点赞")
    void beLikePost(@NonNull PostLikeResponse response);

    @MessageType(value = ResponseMessageType.Post.COMMENT_POST, desc = "post被评论")
    void beCommentPost(@NonNull PostCommentResponse response);

    // 暂时取消开发功能
//    @MessageType(value = ResponseMessageType.Post.COLLECT_POST, desc = "post被收藏")
//    void beCollectPost(@NonNull PostLikeResponse response);

    @MessageType(value = ResponseMessageType.Post.FORWARD_POST, desc = "post被转发")
    void beForwardPost(@NonNull PostForwardResponse response);
}
