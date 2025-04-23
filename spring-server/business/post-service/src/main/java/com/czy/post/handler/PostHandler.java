package com.czy.post.handler;

import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.socket.request.PostCollectRequest;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import com.czy.api.domain.dto.socket.request.PostForwardRequest;
import com.czy.api.domain.dto.socket.request.PostLikeRequest;
import com.czy.post.handler.api.PostApi;
import com.czy.springUtils.annotation.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/23 14:53
 */
@HandlerType(RequestMessageType.Post.root)
@Slf4j
@RequiredArgsConstructor
@Component
public class PostHandler implements PostApi {

    // postRequest -> nettyResponse


    @Override
    public void postCollect(PostCollectRequest request) {

    }

    @Override
    public void postComment(PostCommentRequest request) {

    }

    @Override
    public void postForward(PostForwardRequest request) {

    }

    @Override
    public void postLike(PostLikeRequest request) {

    }
}
