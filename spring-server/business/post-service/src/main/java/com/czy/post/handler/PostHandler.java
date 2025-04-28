package com.czy.post.handler;

import com.czy.api.api.user.UserService;
import com.czy.api.constant.netty.NettyOptionEnum;
import com.czy.api.constant.netty.NettyResponseStatuesEnum;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostCommentConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.base.NettyOptionRequest;
import com.czy.api.domain.dto.socket.request.PostCollectRequest;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import com.czy.api.domain.dto.socket.request.PostFolderRequest;
import com.czy.api.domain.dto.socket.request.PostForwardRequest;
import com.czy.api.domain.dto.socket.request.PostLikeRequest;
import com.czy.api.domain.dto.socket.response.NettyServerResponse;
import com.czy.api.domain.dto.socket.response.PostCommentResponse;
import com.czy.api.domain.dto.socket.response.PostForwardResponse;
import com.czy.post.component.RabbitMqSender;
import com.czy.post.handler.api.PostApi;
import com.czy.post.service.PostCommentService;
import com.czy.post.service.PostHandleService;
import com.czy.post.service.PostService;
import com.czy.springUtils.annotation.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * @author 13225
 * @date 2025/4/23 14:53
 */
@HandlerType(RequestMessageType.Post.root)
@Slf4j
@RequiredArgsConstructor
@Component
public class PostHandler implements PostApi{

    // postRequest -> nettyResponse

    private final PostHandleService postHandleService;
    private final RabbitMqSender rabbitMqSender;
    private final PostCommentConverter postCommentConverter;
    private final PostService postService;
    private final PostCommentService postCommentService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    private boolean checkOption(NettyOptionRequest request){
        if (request == null){
            return false;
        }
        if (!StringUtils.hasText(request.getSenderId())){
            log.warn("发送者id为空");
            return false;
        }
        if (request.getOptionCode() == NettyOptionEnum.NULL.getCode()){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            nettyServerResponse.setMessage("操作类型不能为null");
            rabbitMqSender.push(nettyServerResponse);
            return false;
        }
        return true;
    }

    @Override
    public void postCollect(PostCollectRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        boolean isOptionLegal = checkOption(request);
        if (!isOptionLegal){
            return;
        }
        if (ObjectUtils.isEmpty(request.getPostId())){
            return;
        }
        // 收藏帖子
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            try {
                UserDo userDo = userService.getUserByAccount(request.getSenderId());
                Long folderId = request.getFolderId();
                if (folderId == null || folderId == 0L){
                    // 创建文件夹
                    folderId = postHandleService.createPostCollectFolder(userDo.getId(), PostConstant.DEFAULT_COLLECT_FOLDER_NAME);
                }
                postHandleService.postCollect(request.getPostId(), folderId);
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 取消收藏
        if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            try {
                Long folderId = request.getFolderId();
                if (folderId == null){
                    return;
                }
                postHandleService.deletePostCollect(request.getPostId(), folderId);
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 修改收藏夹
        if (request.getOptionCode() == NettyOptionEnum.UPDATE.getCode()){
            try {
                Long folderId = request.getFolderId();
                if (folderId == null || request.getNewFolderId() == null){
                    return;
                }
                postHandleService.postCollectUpdate(request.getPostId(), folderId, request.getNewFolderId());
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
    }

    @Override
    public void collectFolder(PostFolderRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        boolean isOptionLegal = checkOption(request);
        if (!isOptionLegal){
            return;
        }
        // 创建收藏夹
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            try {
                UserDo userDo = userService.getUserByAccount(request.getSenderId());
                if (userDo == null || userDo.getId() == null){
                    return;
                }
                if (!StringUtils.hasText(request.getName())){
                    return;
                }
                postHandleService.createPostCollectFolder(userDo.getId(), request.getName());
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 删除收藏夹
        if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            try {
                UserDo userDo = userService.getUserByAccount(request.getSenderId());
                if (userDo == null || userDo.getId() == null){
                    return;
                }
                Long folderId = request.getFolderId();
                if (folderId == null){
                    return;
                }
                postHandleService.deletePostCollectFolder(folderId, userDo.getId());
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 更改搜藏夹
        if (request.getOptionCode() == NettyOptionEnum.UPDATE.getCode()){
            try {
                UserDo userDo = userService.getUserByAccount(request.getSenderId());
                if (userDo == null || userDo.getId() == null){
                    return;
                }
                if (request.getFolderId() == null || request.getNewName() == null){
                    return;
                }
                postHandleService.updatePostCollectFolder(request.getFolderId(), userDo.getId(), request.getNewName());
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }

        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
    }

    @Override
    public void postComment(PostCommentRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        boolean isOptionLegal = checkOption(request);
        if (!isOptionLegal){
            return;
        }
        PostCommentResponse postCommentResponse = new PostCommentResponse();
        postCommentResponse.setOptionCode(request.getOptionCode());
        postCommentResponse.setCommentId(request.getCommentId());
        postCommentResponse.setPostId(request.getPostId());
        postCommentResponse.setContent(request.getContent());
        postCommentResponse.setReplyCommentId(request.getReplyCommentId());
        postCommentResponse.setSenderId(request.getSenderId());
        // 这里的receiverId管你是什么都不用
//        postCommentResponse.setReceiverId(request.getReceiverId());
        postCommentResponse.setTimestamp(request.getTimestamp());
        postCommentResponse.setType(request.getType());
        // 发布评论
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            try {
                UserDo userDo = userService.getUserByAccount(request.getSenderId());
                if (userDo == null || userDo.getId() == null){
                    return;
                }
                PostCommentDo postCommentDo = postCommentConverter.postCommentRequestToPostCommentDo(request, userDo.getId());
                // TODO 考虑先存在Redis然后定时批量导入，评论这种东西放在Redis就行了
                postHandleService.postComment(postCommentDo);
                // 通知作者 + 评论发布者
                // 先通知作者
                notifyAuthor(postCommentResponse);
                // 再通知评论发布者
                notifyCommenter(postCommentResponse);
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 删除评论
        if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            try {
                Long postId = request.getPostId();
                Long commentId = request.getCommentId();
                if (postId == null || commentId == null){
                    return;
                }
                // 删除评论不需要netty通知，抖音也是这样做的
                postHandleService.deleteComment(postId, commentId);
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }

        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
    }

    // 通知作者
    private void notifyAuthor(PostCommentResponse postCommentResponse){
        PostAo postAo = postService.findPostById(postCommentResponse.getPostId());
        if (postAo == null || postAo.getAuthorId() == null){
            return;
        }
        UserDo authorDo = userService.getUserById(postAo.getAuthorId());
        if (authorDo == null || !StringUtils.hasText(authorDo.getAccount())){
            return;
        }
        postCommentResponse.setReceiverId(authorDo.getAccount());
        rabbitMqSender.push(postCommentResponse);
    }

    // 通知评论发布者
    private void notifyCommenter(PostCommentResponse postCommentResponse){
        if (postCommentResponse.getReplyCommentId() != null){
            PostCommentDo postCommenterDo = postCommentService.getPostCommentById(postCommentResponse.getReplyCommentId());
            if (postCommenterDo == null || postCommenterDo.getCommenterId() == null){
                return;
            }
            UserDo commenterDo = userService.getUserById(postCommenterDo.getCommenterId());
            if (commenterDo == null || !StringUtils.hasText(commenterDo.getAccount())){
                return;
            }
            postCommentResponse.setReceiverId(commenterDo.getAccount());
            rabbitMqSender.push(postCommentResponse);
        }
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
