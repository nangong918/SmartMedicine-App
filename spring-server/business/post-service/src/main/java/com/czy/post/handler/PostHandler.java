package com.czy.post.handler;

import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.feature.PostOperation;
import com.czy.api.constant.netty.KafkaConstant;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.NettyOptionEnum;
import com.czy.api.constant.netty.NettyResponseStatuesEnum;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostCommentConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.base.NettyOptionRequest;
import com.czy.api.domain.dto.service.CommentResultDto;
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
import com.czy.api.domain.dto.socket.response.PostOperationBaseResponse;
import com.czy.api.domain.entity.kafkaMessage.UserActionCommentPost;
import com.czy.api.domain.entity.kafkaMessage.UserActionOperatePost;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.PostExceptions;
import com.czy.api.exception.UserExceptions;
import com.czy.api.utils.NettyUtils;
import com.czy.post.component.KafkaSender;
import com.czy.post.handler.api.PostApi;
import com.czy.post.mapper.mysql.PostInfoMapper;
import com.czy.post.mq.sender.RabbitMqSender;
import com.czy.post.service.PostCommentService;
import com.czy.post.service.PostHandleService;
import com.czy.post.service.PostService;
import com.czy.springUtils.annotation.HandlerType;
import exception.ExceptionEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;


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
    private final PostInfoMapper postInfoMapper;
    private final PostCommentService postCommentService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    private final KafkaSender kafkaSender;

    private boolean checkOption(NettyOptionRequest request){
        if (request == null){
            return false;
        }
        if (request.getSenderId() == null){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            nettyServerResponse.setError(UserExceptions.USER_NOT_EXIST);
            return false;
        }
        if (request.getOptionCode() == NettyOptionEnum.NULL.getCode()){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 操作行为异常
            nettyServerResponse.setError(PostExceptions.OPERATION_TYPE_NOT_EXIST);
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
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 帖子参数不存在
            nettyServerResponse.setError(CommonExceptions.PARAM_ERROR);
            rabbitMqSender.push(nettyServerResponse);
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
        // 1. 参数校验
        boolean isOptionLegal = checkOption(request);
        if (!isOptionLegal){
            return;
        }
        if (request.getPostId() == null){
            return;
        }
        if (ObjectUtils.isEmpty(request.getPostId())){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 帖子参数不存在
            nettyServerResponse.setError(CommonExceptions.PARAM_ERROR);
            rabbitMqSender.push(nettyServerResponse);
            return;
        }

        // 2. 数据操作
        // 发布评论
        if (NettyOptionEnum.ADD.getCode() == request.getOptionCode()){
            String content = request.getContent();
            if (!StringUtils.hasText(content)){
                // Mq -> sender Error
                NettyUtils.sentErrorMessage(
                        request.getSenderId(),
                        PostExceptions.EMPTY_COMMENT_ERROR,
                        rabbitMqSender
                );
            }
            Long senderId = request.getSenderId();
            Long postId = request.getPostId();
            Long replyCommentId = request.getReplyCommentId();

            // 执行评论
            CommentResultDto resultDto = postCommentService.comment(senderId, postId, replyCommentId, content);

            if (resultDto.isSuccess()){
                // 获取返回对象
                PostCommentResponse response = new PostCommentResponse();
                // 从request获取其他属性
                sePostCommentResponseByRequest(response, request);
                response.setCommentId(request.getCommentId());
                response.setPostId(postId);
                response.setContent(content);
                response.setReplyCommentId(replyCommentId);

                // 需要通知被评论的人 (在此之前需要设置postId)
                notifyAllUsers(request, replyCommentId, response);
            }
            else {
                ExceptionEnums exceptionEnums = Optional.ofNullable(resultDto.getExceptionEnums())
                        .orElse(PostExceptions.COMMENT_ERROR);

                // Mq -> sender Error
                NettyUtils.sentErrorMessage(
                        request.getSenderId(),
                        exceptionEnums,
                        rabbitMqSender
                );
            }
        }
        // 删除评论
        else if (NettyOptionEnum.DELETE.getCode() == request.getOptionCode()){
            Long commentId = request.getCommentId();
            if (commentId == null){
                // Mq -> sender Error
                NettyUtils.sentErrorMessage(
                        request.getSenderId(),
                        PostExceptions.DELETE_COMMENT_ERROR,
                        rabbitMqSender
                );
            }
            else {
                // 数据库操作
                postCommentService.deleteComment(request.getPostId(), commentId, request.getSenderId());

                // 告知请求者删除成功
                PostCommentResponse response = new PostCommentResponse();
                // 发送者是系统服务器; 接收者是请求者
                response.setSenderId(NettyConstants.SERVER_ID);
                response.setReceiverId(request.getSenderId());
                // 从request获取其他属性
                sePostCommentResponseByRequest(response, request);

                // 通知操作申请者
                rabbitMqSender.push(response);
            }
        }

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

    private void sePostCommentResponseByRequest(
            @NotNull PostOperationBaseResponse response,
            @NotNull NettyOptionRequest request
    ){
        // 可以选择此处设置自动去转换，也可以自行设置
        response.setType(request.getType());
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));
        // 操作类型获取
        response.setOptionCode(request.getOptionCode());
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

    /**
     * 通知所有用户   负责将数据发送给消息队列，然后通知用户。
     * 在调用之前response中需要先设置好属性。
     * 此方法只执行：非独特响应体数据赋值，寻址，发送，独特响应体数据设置需要提前设置。
     * @param request           请求
     * @param replyCommentId    回复的评论id
     * @param response          响应体
     */
    private void notifyAllUsers(NettyOptionRequest request, @Nullable Long replyCommentId, PostOperationBaseResponse response){
        sePostCommentResponseByRequest(response, request);

        // 1. 通知发送者
        response.setSenderId(NettyConstants.SERVER_ID);
        response.setReceiverId(request.getSenderId());
        // sever -> sender
        rabbitMqSender.push(response);

        // 2. 通知接收者
        if (replyCommentId != null){
            PostCommentDo postCommenterDo = postCommentService.getPostCommentById(replyCommentId);
            if (postCommenterDo != null && postCommenterDo.getId() != null){
                Long commenterId = Optional.ofNullable(postCommenterDo.getCommenterId())
                                .orElse(NettyConstants.ERROR_ID);
                if (!NettyConstants.ERROR_ID.equals(commenterId)){
                    response.setSenderId(request.getSenderId());
                    response.setReceiverId(commenterId);
                    // sender -> receiver
                    rabbitMqSender.push(response);
                }
            }
        }

        // 3. 通知作者
        if (response.getPostId() != null){
            response.setSenderId(request.getSenderId());
            PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(response.getPostId());
            if (postInfoDo != null && postInfoDo.getAuthorId() != null){
                response.setReceiverId(postInfoDo.getAuthorId());
                // sender -> poster
                rabbitMqSender.push(response);
            }
        }
    }

    @Override
    public void postForward(PostForwardRequest request) {
        NettyResponseStatuesEnum isSuccess = NettyResponseStatuesEnum.SUCCESS;

        // 参数校验
        if (ObjectUtils.isEmpty(request.getPostId())){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 帖子参数不存在
            nettyServerResponse.setError(CommonExceptions.PARAM_ERROR);
            rabbitMqSender.push(nettyServerResponse);
            return;
        }

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
        if (ObjectUtils.isEmpty(request.getPostId())){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 帖子参数不存在
            nettyServerResponse.setError(CommonExceptions.PARAM_ERROR);
            rabbitMqSender.push(nettyServerResponse);
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
//                postLikeResponse.setLikeUserId(request.getSenderId());
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
        if (ObjectUtils.isEmpty(request.getPostId())){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 帖子参数不存在
            nettyServerResponse.setError(CommonExceptions.PARAM_ERROR);
            rabbitMqSender.push(nettyServerResponse);
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
//                postLikeResponse.setLikeUserId(request.getSenderId());
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
