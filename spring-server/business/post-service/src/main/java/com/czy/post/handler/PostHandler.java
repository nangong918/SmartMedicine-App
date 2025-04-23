package com.czy.post.handler;

import com.czy.api.api.user.UserService;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.dto.socket.request.PostCollectRequest;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import com.czy.api.domain.dto.socket.request.PostForwardRequest;
import com.czy.api.domain.dto.socket.request.PostLikeRequest;
import com.czy.api.domain.dto.socket.response.NettyServerResponse;
import com.czy.post.handler.api.PostApi;
import com.czy.post.service.PostHandleService;
import com.czy.post.service.PostService;
import com.czy.springUtils.annotation.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
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
    // TODO 1.this
    // TODO 2.netty -> mq -> listener -> springEvent -> springEventListener

    private final PostService postService;
    private final PostHandleService postHandleService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    @Override
    public void postCollect(PostCollectRequest request) {
        String isSuccess = ResponseMessageType.SUCCESS;
        try {
            UserDo userDo = userService.getUserByAccount(request.getSenderId());
            Long folderId = request.getFolderId();
            if (request.getFolderId() == null || request.getFolderId() == 0L){
                // 创建文件夹
                folderId = postHandleService.createPostCollectFolder(userDo.getId(), PostConstant.DEFAULT_COLLECT_FOLDER_NAME);
            }
            postHandleService.postCollect(request.getPostId(), folderId);
        } catch (Exception e){
            isSuccess = ResponseMessageType.FAILURE;
        }
        // netty通知前端
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess);
        nettyServerResponse.setBaseRequestData(request);
        // Mq -> user
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
