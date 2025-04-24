package com.czy.post.handler;

import com.czy.api.api.user.UserService;
import com.czy.api.constant.netty.NettyResponseStatuesEnum;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostCommentConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.dto.socket.request.PostCollectRequest;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import com.czy.api.domain.dto.socket.request.PostForwardRequest;
import com.czy.api.domain.dto.socket.request.PostLikeRequest;
import com.czy.api.domain.dto.socket.response.NettyServerResponse;
import com.czy.api.domain.dto.socket.response.PostForwardResponse;
import com.czy.post.component.RabbitMqSender;
import com.czy.post.handler.api.PostApi;
import com.czy.post.service.PostHandleService;
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

    private final PostHandleService postHandleService;
    private final RabbitMqSender rabbitMqSender;
    private final PostCommentConverter postCommentConverter;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    @Override
    public void postCollect(PostCollectRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        try {
            UserDo userDo = userService.getUserByAccount(request.getSenderId());
            Long folderId = request.getFolderId();
            if (request.getFolderId() == null || request.getFolderId() == 0L){
                // 创建文件夹
                folderId = postHandleService.createPostCollectFolder(userDo.getId(), PostConstant.DEFAULT_COLLECT_FOLDER_NAME);
            }
            postHandleService.postCollect(request.getPostId(), folderId);
        } catch (Exception e){
            isSuccess = NettyResponseStatuesEnum.FAILURE;
        }
        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
    }

    @Override
    public void postComment(PostCommentRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        try {
            String userAccount = request.getSenderId();
            UserDo userDo = userService.getUserByAccount(userAccount);
            if (userDo == null){
                String warningMessage = String.format("用户不存在，account: %s", userAccount);
                log.warn(warningMessage);
                isSuccess = NettyResponseStatuesEnum.FAILURE;
                // netty通知前端 内部会设置发送id是serverId
                NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
                // Mq -> user
                rabbitMqSender.push(nettyServerResponse);
                return;
            }
            // TODO 考虑先存在Redis然后定时批量导入，评论这种东西放在Redis就行了
            Long commenterId = userDo.getId();
            PostCommentDo postCommentDo = postCommentConverter.postCommentRequestToPostCommentDo(request, commenterId);
            postHandleService.postComment(postCommentDo);
        } catch (Exception e){
            isSuccess = NettyResponseStatuesEnum.FAILURE;
        }
        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
    }

    @Override
    public void postForward(PostForwardRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        try {
            UserDo senderDo = userService.getUserByAccount(request.getSenderId());
            UserDo receiverDo = userService.getUserByAccount(request.getReceiverId());
            if (senderDo == null || receiverDo == null){
                String warningMessage = String.format("用户不存在，sender: %s; receiver: %s", request.getSenderId(), request.getReceiverId());
                log.warn(warningMessage);
                isSuccess = NettyResponseStatuesEnum.FAILURE;
                // netty通知前端 内部会设置发送id是serverId
                NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
                // Mq -> user
                rabbitMqSender.push(nettyServerResponse);
                return;
            }
            postHandleService.postForward(request.getPostId());
        } catch (Exception e){
            isSuccess = NettyResponseStatuesEnum.FAILURE;
        }
        // netty先通知sender
        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
        // netty通知receiver
        PostForwardResponse postForwardResponse = new PostForwardResponse(request.getPostId());
        postForwardResponse.initResponseByRequest(request);
        // Mq -> user 发送方法中包含转换方法
        rabbitMqSender.push(postForwardResponse);
    }

    @Override
    public void postLike(PostLikeRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        try {
            UserDo userDo = userService.getUserByAccount(request.getSenderId());
            if (userDo == null){
                String warningMessage = String.format("用户不存在，account: %s", request.getSenderId());
                log.warn(warningMessage);
                isSuccess = NettyResponseStatuesEnum.FAILURE;
                // netty通知前端 内部会设置发送id是serverId
                NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
                // Mq -> user
                rabbitMqSender.push(nettyServerResponse);
                return;
            }
            postHandleService.postLike(request.getPostId(), userDo.getId());
        } catch (Exception e){
            isSuccess = NettyResponseStatuesEnum.FAILURE;
        }
        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
    }
}
