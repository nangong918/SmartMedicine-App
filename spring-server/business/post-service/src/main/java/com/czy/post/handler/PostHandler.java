package com.czy.post.handler;

import com.api.mapper.post.mysql.PostInfoMapper;
import com.czy.api.api.user_relationship.user.UserService;
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
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.base.NettyOptionRequest;
import com.czy.api.domain.dto.service.CollectOperateResultDto;
import com.czy.api.domain.dto.service.CommentResultDto;
import com.czy.api.domain.dto.socket.request.PostCollectRequest;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import com.czy.api.domain.dto.socket.request.PostDisLikeRequest;
import com.czy.api.domain.dto.socket.request.PostFolderRequest;
import com.czy.api.domain.dto.socket.request.PostForwardRequest;
import com.czy.api.domain.dto.socket.request.PostLikeRequest;
import com.czy.api.domain.dto.socket.response.CollectionOperateResponse;
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
import com.czy.post.mq.sender.RabbitMqSender;
import com.czy.post.service.PostCommentService;
import com.czy.post.service.PostHandleService;
import com.czy.post.service.PostService;
import com.utils.rabbitmq.annotation.HandlerType;
import exception.ExceptionEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.NotNull;
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

    private boolean checkParams(NettyOptionRequest request){
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
        boolean isOptionLegal = checkParams(request);
        if (!isOptionLegal){
            NettyUtils.sendErrorMessage(request.getSenderId(), CommonExceptions.PARAM_ERROR, rabbitMqSender);
            return;
        }
        if (ObjectUtils.isEmpty(request.getPostId())){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 帖子参数不存在
            nettyServerResponse.setError(CommonExceptions.PARAM_ERROR);
            rabbitMqSender.push(nettyServerResponse);
            return;
        }

        NettyOptionEnum nettyOptionEnum = NettyOptionEnum.getByCode(request.getOptionCode());
        switch (nettyOptionEnum){
            // 收藏
            case ADD: {
                // 收藏
                Long folderId = request.getFolderId();
                if (folderId == null || NettyConstants.ERROR_ID.equals(folderId)){
                    // 查找/创建文件夹
                    folderId = postHandleService.createPostCollectFolder(request.getSenderId(), PostConstant.DEFAULT_COLLECT_FOLDER_NAME);
                }
                postHandleService.postCollect(request.getPostId(), folderId);

                // 发送消息
                NettyUtils.sendSuccessMessage(
                        request.getSenderId(),
                        "收藏成功",
                        rabbitMqSender
                );
                break;
            }
            // 取消收藏
            case DELETE: {
                Long folderId = request.getFolderId();
                if (folderId == null){
                    return;
                }

                // 取消收藏
                postHandleService.deletePostCollect(request.getPostId(), folderId);

                // 发送消息
                NettyUtils.sendSuccessMessage(
                        request.getSenderId(),
                        "取消收藏成功",
                        rabbitMqSender
                );
            }
            // 更改收藏
            case UPDATE: {
                Long folderId = request.getFolderId();
                if (folderId == null || request.getNewFolderId() == null){
                    return;
                }
                postHandleService.postCollectUpdate(request.getPostId(), folderId, request.getNewFolderId());
            }
            // 未知
            default: {
                NettyUtils.sendErrorMessage(
                        request.getSenderId(),
                        PostExceptions.OPERATION_TYPE_NOT_EXIST,
                        rabbitMqSender
                );
                return;
            }
        }

        // 通过netty operation -> 帖子操作类型
        PostOperation operateEnum = Optional.of(request.getOptionCode())
                .map(code -> {
                    NettyOptionEnum optionEnum = NettyOptionEnum.getByCode(code);
                    switch (optionEnum){
                        case ADD:
                            return PostOperation.COLLECT;
                        case DELETE:
                            return PostOperation.CANCEL_COLLECT;
                        default:
                            return PostOperation.NULL;
                    }
                })
                .orElse(PostOperation.NULL);

        // kafka -> log -> feature
        if (!PostOperation.NULL.equals(operateEnum)){
            UserActionOperatePost userActionOperatePost = new UserActionOperatePost();
            userActionOperatePost.setUserId(request.getSenderId());
            userActionOperatePost.setPostId(request.getPostId());
            userActionOperatePost.setOperateType(operateEnum.getCode());

            try {
                kafkaSender.sendUserActionMessage(userActionOperatePost, KafkaConstant.Topic.Post_Operation);
            } catch (Exception e) {
                log.error("用户显性行为Kafka传输异常：[收藏] [userId:{}] [postId:{}]", request.getSenderId(), request.getPostId(), e);
            }
        }
    }

    @Override
    public void collectFolder(PostFolderRequest request) {
        boolean isOptionLegal = checkParams(request);
        if (!isOptionLegal){
            NettyUtils.sendErrorMessage(request.getSenderId(), CommonExceptions.PARAM_ERROR, rabbitMqSender);
            return;
        }

        CollectOperateResultDto resultDto = operateCollectFolder(request);
        boolean isSuccess = resultDto.isSuccess();
        if (isSuccess){
            if (resultDto.getOptionEnum().equals(NettyOptionEnum.ADD)){
                Long collectionFolderId = resultDto.getCollectFolderId();
                if (collectionFolderId == null){
                    // id 为 null 创建收藏夹失败
                    NettyUtils.sendErrorMessage(request.getSenderId(), PostExceptions.CREATE_COLLECT_FOLDER_FAILED, rabbitMqSender);
                }
                else {
                    CollectionOperateResponse response = new CollectionOperateResponse();
                    response.setCollectionFolderId(collectionFolderId);
                    response.setSenderId(NettyConstants.SERVER_ID);
                    response.setReceiverId(request.getSenderId());
                    response.setOptionCode(NettyOptionEnum.ADD.getCode());
                    response.setTimestamp(String.valueOf(System.currentTimeMillis()));
                    rabbitMqSender.push(response);
                }
            }
            else {
                NettyUtils.sendSuccessMessage(request.getSenderId(), "操作成功", rabbitMqSender);
            }
        }
        else {
            NettyUtils.sendErrorMessage(request.getSenderId(), PostExceptions.COLLECT_FOLDER_OPERATION_FAILED, rabbitMqSender);
        }
    }

    /**
     * 操作收藏夹
     * @param request   收藏夹请求
     * @return          是否成功
     */
    private CollectOperateResultDto operateCollectFolder(PostFolderRequest request){
        NettyOptionEnum optionEnum = NettyOptionEnum.getByCode(request.getOptionCode());
        switch (optionEnum) {
            case ADD: {
                try {
                    if (!StringUtils.hasText(request.getName())){
                        return new CollectOperateResultDto();
                    }
                    // 如果存在的话，直接返回的就是存在的id
                    Long collectFolderId = postHandleService.createPostCollectFolder(
                            request.getSenderId(),
                            request.getName()
                    );
                    if (collectFolderId != null){
                        return new CollectOperateResultDto(collectFolderId);
                    }
                    else {
                        return new CollectOperateResultDto(optionEnum);
                    }
                } catch (Exception e){
                    log.error("创建收藏夹失败", e);
                    return new CollectOperateResultDto(optionEnum);
                }
            }
            case DELETE: {
                try {
                    Long folderId = request.getFolderId();
                    if (folderId == null){
                        return new CollectOperateResultDto(optionEnum);
                    }
                    postHandleService.deletePostCollectFolder(folderId, request.getSenderId());
                    return new CollectOperateResultDto(optionEnum, true);
                } catch (Exception e){
                    log.error("删除收藏夹失败", e);
                    return new CollectOperateResultDto(optionEnum);
                }
            }
            case UPDATE: {
                try {
                    if (request.getFolderId() == null || request.getNewName() == null){
                        return new CollectOperateResultDto();
                    }
                    postHandleService.updatePostCollectFolder(request.getFolderId(), request.getSenderId(), request.getNewName());
                    return new CollectOperateResultDto(optionEnum, true);
                } catch (Exception e){
                    log.error("更新收藏夹失败", e);
                    return new CollectOperateResultDto(optionEnum);
                }
            }
            default: {
                return new CollectOperateResultDto(optionEnum);
            }
        }
    }

    // 评论帖子： netty响应三方：1.评论者（成功评论）2.帖子作者（帖子被评论）3.被评论者（收到评论）
    @Override
    public void postComment(PostCommentRequest request) {
        // 1. 参数校验
        boolean isOptionLegal = checkParams(request);
        if (!isOptionLegal || ObjectUtils.isEmpty(request.getPostId())){
            NettyUtils.sendErrorMessage(request.getSenderId(), CommonExceptions.PARAM_ERROR, rabbitMqSender);
            return;
        }

        // 2. 数据操作
        // 发布评论
        if (NettyOptionEnum.ADD.getCode() == request.getOptionCode()){
            String content = request.getContent();
            if (!StringUtils.hasText(content)){
                // Mq -> sender Error
                NettyUtils.sendErrorMessage(
                        request.getSenderId(),
                        PostExceptions.EMPTY_COMMENT_ERROR,
                        rabbitMqSender
                );
                return;
            }
            if (content.length() > PostConstant.COMMENT_MAX_LENGTH){
                NettyUtils.sendErrorMessage(
                        request.getSenderId(),
                        PostExceptions.COMMENT_TOO_LONG,
                        rabbitMqSender
                );
                return;
            }
            Long senderId = request.getSenderId();
            Long postId = request.getPostId();
            Long replyCommentId = request.getReplyCommentId();

            Long timestamp = Optional.ofNullable(request.getTimestamp())
                    .map(tstr -> {
                        try {
                            return Long.valueOf(tstr);
                        } catch (Exception e) {
                            return System.currentTimeMillis();
                        }
                    })
                    .orElse(System.currentTimeMillis());

            // 执行评论
            CommentResultDto resultDto = postCommentService.comment(senderId, postId, replyCommentId, content, timestamp);

            if (resultDto.isSuccess()){
                // 获取返回对象
                PostCommentResponse response = new PostCommentResponse();
                // 从request获取其他属性
                response.setCommentId(request.getCommentId());
                response.setPostId(postId);
                response.setContent(content);
                response.setReplyCommentId(replyCommentId);

                // 需要通知被评论的人 (在此之前需要设置postId)
                notifyAllUsersComment(request, response);
            }
            else {
                ExceptionEnums exceptionEnums = Optional.ofNullable(resultDto.getExceptionEnums())
                        .orElse(PostExceptions.COMMENT_ERROR);

                // Mq -> sender Error
                NettyUtils.sendErrorMessage(
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
                NettyUtils.sendErrorMessage(
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
                setPostOperationBaseResponseByRequest(response, request);

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

    private void setPostOperationBaseResponseByRequest(
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
     * @param response          响应体
     */
    private void notifyAllUsersComment(PostCommentRequest request, PostOperationBaseResponse response){
        // 初始化值
        setPostOperationBaseResponseByRequest(response, request);

        // 1. 通知发送者
        response.setSenderId(NettyConstants.SERVER_ID);
        response.setReceiverId(request.getSenderId());
        // sever -> sender
        rabbitMqSender.push(response);

        // 2. 通知接收者
        if (request.getReplyCommentId() != null){
            PostCommentDo postCommenterDo = postCommentService.getPostCommentById(request.getReplyCommentId());
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

    private void notifyAllUsersForward(PostForwardRequest request, PostOperationBaseResponse response){
        // 1. 参数校验
        if (request.getToUserId() == null){
            NettyUtils.sendErrorMessage(
                    request.getSenderId(),
                    CommonExceptions.PARAM_ERROR,
                    rabbitMqSender
            );
            return;
        }
        // 获取接收者
        UserDo receiverDo = userService.getUserById(request.getToUserId());
        if (receiverDo == null || receiverDo.getId() == null){
            NettyUtils.sendErrorMessage(
                    request.getSenderId(),
                    UserExceptions.USER_NOT_EXIST,
                    rabbitMqSender
            );
            return;
        }

        // 2. 初始化值
        setPostOperationBaseResponseByRequest(response, request);

        // 3. 发送给接收者
        response.setSenderId(request.getSenderId());
        response.setReceiverId(request.getReceiverId());
        rabbitMqSender.push(response);

        // 4. 发送给发送者，说明成功了
        NettyUtils.sendSuccessMessage(
                request.getSenderId(),
                NettyResponseStatuesEnum.SUCCESS.getMessage(),
                rabbitMqSender
        );

        // 5. 发送给作者
        Long postId = Optional.ofNullable(request.getPostId())
                .orElse(NettyConstants.ERROR_ID);
        if (!postId.equals(NettyConstants.ERROR_ID)){
            PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
            Long authorId = Optional.ofNullable(postInfoDo)
                    .map(PostInfoDo::getAuthorId)
                    .orElse(NettyConstants.ERROR_ID);
            if (!authorId.equals(NettyConstants.ERROR_ID)){
                response.setReceiverId(authorId);
                // sender -> receiver
                rabbitMqSender.push(response);
            }
        }
    }

    @Override
    public void postForward(PostForwardRequest request) {
        // 参数校验
        boolean isOptionLegal = checkParams(request);
        if (!isOptionLegal){
            NettyUtils.sendErrorMessage(request.getSenderId(), CommonExceptions.PARAM_ERROR, rabbitMqSender);
            return;
        }
        if (ObjectUtils.isEmpty(request.getPostId())){
            NettyServerResponse nettyServerResponse = new NettyServerResponse(NettyResponseStatuesEnum.FAILURE);
            // 帖子参数不存在
            nettyServerResponse.setError(CommonExceptions.PARAM_ERROR);
            rabbitMqSender.push(nettyServerResponse);
            return;
        }

        // 数据库操作
        postHandleService.postForward(request.getPostId());
        // netty通知前端 内部会设置发送id是serverId
        PostForwardResponse response = new PostForwardResponse(request.getPostId());
        response.setContent(request.getContent());
        response.setSenderId(request.getSenderId());
        // 对前端的receiverId不信任，可能是SERVER_ID，设置为ToUserAccount
        response.setReceiverId(request.getToUserId());

        // 通知 全部的人
        notifyAllUsersForward(request, response);

        // userAction -> kafka -> feature-service
        UserActionOperatePost userActionOperatePost = new UserActionOperatePost();
        userActionOperatePost.setUserId(request.getSenderId());
        userActionOperatePost.setPostId(request.getPostId());
        userActionOperatePost.setOperateType(PostOperation.FORWARD.getCode());

        try {
            kafkaSender.sendUserActionMessage(userActionOperatePost, KafkaConstant.Topic.Post_Operation);
        } catch (Exception e){
            log.error("用户显性行为Kafka传输异常：[转发] [userId:{}] [postId:{}]", request.getSenderId(), request.getPostId(), e);
        }
    }


    @Override
    public void postLike(PostLikeRequest request) {
        boolean isOptionLegal = checkParams(request);
        if (!isOptionLegal || ObjectUtils.isEmpty(request.getPostId())){
            NettyUtils.sendErrorMessage(request.getSenderId(), CommonExceptions.PARAM_ERROR, rabbitMqSender);
            return;
        }

        // 记录操作行为类型
        Integer operateType = PostOperation.NULL.getCode();
        PostLikeResponse response = new PostLikeResponse(request.getPostId());
        // 点赞
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            // 数据库增加
            postHandleService.postLike(request.getPostId(), request.getSenderId());
            operateType = PostOperation.LIKE.getCode();

            // 通知
            notifyAllUsersLike(request, response);
        }
        // 取消点赞
        else if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            // 数据库减少
            postHandleService.deletePostLike(request.getPostId(), request.getSenderId());
            operateType = PostOperation.CANCEL_LIKE.getCode();

            // 通知
            notifyAllUsersLike(request, response);
        }

        // userAction -> kafka -> feature-service
        if (!operateType.equals(PostOperation.NULL.getCode())){
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
    }

    private void notifyAllUsersLike(PostLikeRequest request, PostLikeResponse response){
        // 从request初始化赋值
        setPostOperationBaseResponseByRequest(response, request);
        response.setPostId(request.getPostId());

        // 参数校验
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(request.getPostId());
        if (postInfoDo == null || postInfoDo.getId() == null){
            NettyUtils.sendErrorMessage(
                    request.getSenderId(),
                    PostExceptions.POST_NOT_EXIST,
                    rabbitMqSender
            );
        }
        // 发送给作者
        else if (postInfoDo.getAuthorId() != null){
            response.setSenderId(request.getSenderId());
            response.setReceiverId(postInfoDo.getAuthorId());
            rabbitMqSender.push(response);
        }

        // 发送给发送者
        response.setSenderId(NettyConstants.SERVER_ID);
        response.setReceiverId(request.getSenderId());
        rabbitMqSender.push(response);
    }

    @Override
    public void notInterested(PostDisLikeRequest request) {
        boolean isOptionLegal = checkParams(request);
        if (!isOptionLegal || ObjectUtils.isEmpty(request.getPostId())){
            NettyUtils.sendErrorMessage(request.getSenderId(),
                    CommonExceptions.PARAM_ERROR,
                    rabbitMqSender
            );
            return;
        }

        Integer operateType = PostOperation.NULL.getCode();

        // 获取postInfo
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(request.getPostId());
        if (postInfoDo == null || postInfoDo.getId() == null){
            NettyUtils.sendErrorMessage(
                    request.getSenderId(),
                    PostExceptions.POST_NOT_EXIST,
                    rabbitMqSender
            );
            return;
        }

        PostLikeResponse response = new PostLikeResponse(request.getPostId());
        setPostOperationBaseResponseByRequest(response, request);
        response.setSenderId(NettyConstants.SERVER_ID);
        response.setReceiverId(request.getSenderId());

        // 不感兴趣
        if (request.getOptionCode() == NettyOptionEnum.ADD.getCode()){
            // 数据库增加
            postHandleService.postNotInterested(request.getPostId(), request.getSenderId());
            operateType = PostOperation.NOT_INTERESTED.getCode();

            // 通知用户成功
            rabbitMqSender.push(response);
        }
        // 取消不感兴趣
        else if (request.getOptionCode() == NettyOptionEnum.DELETE.getCode()){
            // 数据库扣减
            postHandleService.deletePostNotInterested(request.getPostId(), request.getSenderId());
            operateType = PostOperation.CANCEL_NOT_INTERESTED.getCode();

            // 通知用户成功
            rabbitMqSender.push(response);
        }

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
}
