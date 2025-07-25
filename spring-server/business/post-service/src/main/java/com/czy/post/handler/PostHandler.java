package com.czy.post.handler;

import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.feature.PostOperation;
import com.czy.api.constant.netty.KafkaConstant;
import com.czy.api.constant.netty.NettyOptionEnum;
import com.czy.api.constant.netty.NettyResponseStatuesEnum;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostCommentConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.base.NettyOptionRequest;
import com.czy.api.domain.dto.socket.request.PostCollectRequest;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import com.czy.api.domain.dto.socket.request.PostDisLikeRequest;
import com.czy.api.domain.dto.socket.request.PostFolderRequest;
import com.czy.api.domain.dto.socket.request.PostForwardRequest;
import com.czy.api.domain.dto.socket.request.PostLikeRequest;
import com.czy.api.domain.dto.socket.response.NettyServerResponse;
import com.czy.api.domain.dto.socket.response.PostCommentResponse;
import com.czy.api.domain.dto.socket.response.PostForwardResponse;
import com.czy.api.domain.dto.socket.response.PostLikeResponse;
import com.czy.api.domain.entity.kafkaMessage.UserActionCommentPost;
import com.czy.api.domain.entity.kafkaMessage.UserActionOperatePost;
import com.czy.api.exception.PostExceptions;
import com.czy.api.utils.NettyUtils;
import com.czy.post.component.KafkaSender;
import com.czy.post.handler.api.PostApi;
import com.czy.post.mq.sender.RabbitMqSender;
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

    private final KafkaSender kafkaSender;

    private boolean checkOption(NettyOptionRequest request){
        if (request == null){
            return false;
        }
        if (request.getSenderId() == null){
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
        Integer operateType = PostOperation.NULL.getCode();
        // 收藏帖子
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            try {
                Long folderId = request.getFolderId();
                if (folderId == null || folderId == 0L){
                    // 创建文件夹
                    folderId = postHandleService.createPostCollectFolder(request.getSenderId(), PostConstant.DEFAULT_COLLECT_FOLDER_NAME);
                }
                postHandleService.postCollect(request.getPostId(), folderId);
                operateType = PostOperation.COLLECT.getCode();
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
                operateType = PostOperation.CANCEL_COLLECT.getCode();
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
        // kafka -> log -> feature
        UserActionOperatePost userActionOperatePost = new UserActionOperatePost();
        userActionOperatePost.setUserId(request.getSenderId());
        userActionOperatePost.setPostId(request.getPostId());
        userActionOperatePost.setOperateType(operateType);

        try {
            kafkaSender.sendUserActionMessage(userActionOperatePost, KafkaConstant.Topic.Post_Operation);
        } catch (Exception e) {
            log.error("用户显性行为Kafka传输异常：[收藏] [userId:{}] [postId:{}]", request.getSenderId(), request.getPostId(), e);
        }
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
                if (!StringUtils.hasText(request.getName())){
                    return;
                }
                postHandleService.createPostCollectFolder(request.getSenderId(), request.getName());
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 删除收藏夹
        if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            try {
                Long folderId = request.getFolderId();
                if (folderId == null){
                    return;
                }
                postHandleService.deletePostCollectFolder(folderId, request.getSenderId());
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 更改搜藏夹
        if (request.getOptionCode() == NettyOptionEnum.UPDATE.getCode()){
            try {
                if (request.getFolderId() == null || request.getNewName() == null){
                    return;
                }
                postHandleService.updatePostCollectFolder(request.getFolderId(), request.getSenderId(), request.getNewName());
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }

        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
    }

    // 评论帖子： netty响应三方：1.评论者（成功评论）2.帖子作者（帖子被评论）3.被评论者（收到评论）
    @Override
    public void postComment(PostCommentRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;

        // 1. 参数校验
        boolean isOptionLegal = checkOption(request);
        if (!isOptionLegal){
            return;
        }

        // 2. 数据操作
        // 发布评论
        if (NettyOptionEnum.ADD.getCode() == request.getOptionCode()){

        }
        // 删除评论
        else if (NettyOptionEnum.DELETE.getCode() == request.getOptionCode()){
            Long commentId = request.getCommentId();
            if (commentId == null){
                // Mq -> sender
                NettyUtils.sentErrorMessage(
                        request.getSenderId(),
                        PostExceptions.DELETE_COMMENT_ERROR,
                        rabbitMqSender
                );
            }
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
                PostCommentDo postCommentDo = postCommentConverter.postCommentRequestToPostCommentDo(request, request.getSenderId());
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

        // userAction -> kafka -> feature-service
        UserActionCommentPost userActionCommentPost = new UserActionCommentPost();
        userActionCommentPost.setUserId(request.getSenderId());
        userActionCommentPost.setPostId(request.getPostId());
        userActionCommentPost.setComment(request.getContent());
        try {
            kafkaSender.sendUserActionMessage(userActionCommentPost, KafkaConstant.Topic.Comment);
        } catch (Exception e){
            log.error("用户显性行为Kafka传输异常：[评论] [userId:{}] [postId:{}]", request.getSenderId(), request.getPostId(), e);
        }
    }

    // comment通知作者
    private void notifyAuthor(PostCommentResponse postCommentResponse){
        PostAo postAo = postService.findPostById(postCommentResponse.getPostId());
        if (postAo == null || postAo.getAuthorId() == null){
            return;
        }
        postCommentResponse.setReceiverId(postAo.getAuthorId());
        rabbitMqSender.push(postCommentResponse);
    }

    // comment通知评论发布者
    private void notifyCommenter(PostCommentResponse postCommentResponse){
        if (postCommentResponse.getReplyCommentId() != null){
            PostCommentDo postCommenterDo = postCommentService.getPostCommentById(postCommentResponse.getReplyCommentId());
            if (postCommenterDo == null || postCommenterDo.getCommenterId() == null){
                return;
            }
            postCommentResponse.setReceiverId(postCommenterDo.getCommenterId());
            rabbitMqSender.push(postCommentResponse);
        }
    }

    @Override
    public void postForward(PostForwardRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        Integer operateType = PostOperation.NULL.getCode();
        try {
            // 数据库操作
            postHandleService.postForward(request.getPostId());
            // netty通知前端 内部会设置发送id是serverId
            PostForwardResponse postForwardResponse = new PostForwardResponse(request.getPostId());
            postForwardResponse.setContent(request.getContent());
            postForwardResponse.setSenderId(request.getSenderId());
            // 对前端的receiverId不信任，可能是SERVER_ID，设置为ToUserAccount
            postForwardResponse.setReceiverId(request.getToUserId());
            rabbitMqSender.push(postForwardResponse);
            operateType = PostOperation.FORWARD.getCode();
        } catch (Exception e){
            isSuccess = NettyResponseStatuesEnum.FAILURE;
        }
        // netty通知sender
        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
        // userAction -> kafka -> feature-service
        UserActionOperatePost userActionOperatePost = new UserActionOperatePost();
        userActionOperatePost.setUserId(request.getSenderId());
        userActionOperatePost.setPostId(request.getPostId());
        userActionOperatePost.setOperateType(operateType);

        try {
            kafkaSender.sendUserActionMessage(userActionOperatePost, KafkaConstant.Topic.Post_Operation);
        } catch (Exception e){
            log.error("用户显性行为Kafka传输异常：[转发] [userId:{}] [postId:{}]", request.getSenderId(), request.getPostId(), e);
        }
    }


    @Override
    public void postLike(PostLikeRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;
        Integer operateType = PostOperation.NULL.getCode();
        boolean isOptionLegal = checkOption(request);
        if (!isOptionLegal){
            return;
        }
        // 点赞
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            try {
                // 数据库增加
                postHandleService.postLike(request.getPostId(), request.getSenderId());
                // 通知作者
                PostAo postAo = postService.findPostById(request.getPostId());
                if (postAo == null || postAo.getAuthorId() == null){
                    return;
                }
                PostLikeResponse postLikeResponse = new PostLikeResponse(request.getPostId());
                postLikeResponse.setLikeUserId(request.getSenderId());
                postLikeResponse.setReceiverId(postAo.getAuthorId());
                rabbitMqSender.push(postLikeResponse);
                operateType = PostOperation.LIKE.getCode();
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // 取消点赞
        if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            try {
                postHandleService.deletePostLike(request.getPostId(), request.getSenderId());
                operateType = PostOperation.CANCEL_LIKE.getCode();
            } catch (Exception e){
                isSuccess = NettyResponseStatuesEnum.FAILURE;
            }
        }
        // netty通知前端 内部会设置发送id是serverId
        NettyServerResponse nettyServerResponse = new NettyServerResponse(isSuccess, request);
        // Mq -> user
        rabbitMqSender.push(nettyServerResponse);
        // userAction -> kafka -> feature-service
        UserActionOperatePost userActionOperatePost = new UserActionOperatePost();
        userActionOperatePost.setUserId(request.getSenderId());
        userActionOperatePost.setPostId(request.getPostId());
        userActionOperatePost.setOperateType(operateType);

        try {
            kafkaSender.sendUserActionMessage(userActionOperatePost, KafkaConstant.Topic.Post_Operation);
        } catch (Exception e){
            log.error("用户显性行为Kafka传输异常：[点赞] [userId:{}] [postId:{}]", request.getSenderId(), request.getPostId(), e);
        }
    }

    @Override
    public void notInterested(PostDisLikeRequest request) {
        Integer operateType = PostOperation.NULL.getCode();
        boolean isOptionLegal = checkOption(request);
        if (!isOptionLegal){
            return;
        }
        // 不感兴趣
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            try {
                // 数据库增加
                postHandleService.postNotInterested(request.getPostId(), request.getSenderId());
                // 通知作者
                PostAo postAo = postService.findPostById(request.getPostId());
                if (postAo == null || postAo.getAuthorId() == null){
                    return;
                }
                PostLikeResponse postLikeResponse = new PostLikeResponse(request.getPostId());
                postLikeResponse.setLikeUserId(request.getSenderId());
                postLikeResponse.setReceiverId(postAo.getAuthorId());
                rabbitMqSender.push(postLikeResponse);
                operateType = PostOperation.NOT_INTERESTED.getCode();
            } catch (Exception ignored){
            }
        }
        // 取消不感兴趣
        if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            try {
                postHandleService.deletePostNotInterested(request.getPostId(), request.getSenderId());
                operateType = PostOperation.CANCEL_NOT_INTERESTED.getCode();
            } catch (Exception ignored){
            }
        }

        // userAction -> kafka -> feature-service
        UserActionOperatePost userActionOperatePost = new UserActionOperatePost();
        userActionOperatePost.setPostId(request.getSenderId());
        userActionOperatePost.setOperateType(operateType);

        try {
            kafkaSender.sendUserActionMessage(userActionOperatePost, KafkaConstant.Topic.Post_Operation);
        } catch (Exception e){
            log.error("用户显性行为Kafka传输异常：[点赞] [userId:{}] [postId:{}]", request.getSenderId(), request.getPostId(), e);
        }
    }
}
