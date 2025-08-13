package com.czy.message.handler;


import cn.hutool.core.util.IdUtil;
import com.czy.api.api.message.ChatService;
import com.czy.api.api.user_relationship.user.UserService;
import com.czy.api.constant.MessageTypeEnum;
import com.czy.api.constant.message.MessageConstant;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.bo.message.UserChatLastMessageBo;
import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.dto.http.request.DeleteAllMessageRequest;
import com.czy.api.domain.dto.http.request.SendImageRequest;
import com.czy.api.domain.dto.http.request.SendTextDataRequest;
import com.czy.api.domain.dto.socket.response.UploadFileResponse;
import com.czy.api.domain.dto.socket.response.UserTextDataResponse;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.MessageExceptions;
import com.czy.api.exception.UserExceptions;
import com.czy.api.utils.NettyUtils;
import com.czy.message.handler.api.ChatApi;
import com.czy.message.mapper.mongo.UserChatMessageMongoMapper;
import com.czy.message.mq.sender.RabbitMqSender;
import com.czy.message.queue.ChatMessageQueue;
import com.utils.minio.service.OssService;
import com.utils.rabbitmq.annotation.HandlerType;
import com.utils.rabbitmq.component.RabbitMqErrorSender;
import com.utils.redisson.service.RedissonService;
import exception.NettyException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/3/10 17:04
 * 需要注册到MessageConstant
 */

@HandlerType(RequestMessageType.Chat.root)
@Slf4j
@RequiredArgsConstructor
@Component
public class ChatHandler implements ChatApi {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final OssService ossService;
    private final RabbitMqSender rabbitMqSender;
    private final RabbitMqErrorSender rabbitMqErrorSender;
    private final ChatService chatService;
    private final ChatMessageQueue chatMessageQueue;
    private final UserChatMessageMongoMapper userChatMessageMongoMapper;
    private final RedissonService redissonService;

    private boolean checkParams(@NonNull BaseRequestData request, String content){
        return !request.checkParams() || !StringUtils.hasText(content);
    }

    @Override
    public void sendTextMessageToUser(SendTextDataRequest request) {
        // 参数校验
        if (checkParams(request, request.getContent())){
            NettyUtils.sendErrorMessage(request.getSenderId(), CommonExceptions.PARAM_ERROR, rabbitMqErrorSender);
            throw new NettyException(CommonExceptions.PARAM_ERROR, request.getSenderId());
        }
        UserDo senderDo = userService.getUserById(request.getSenderId());
        UserDo receiverDo = userService.getUserById(request.getReceiverId());

        if (senderDo == null || receiverDo == null || senderDo.getId() == null || receiverDo.getId() == null){
            NettyUtils.sendErrorMessage(
                    request.getSenderId(),
                    UserExceptions.USER_NOT_EXIST,
                    rabbitMqErrorSender
            );
            // 交给全局异常处理
            throw new NettyException(UserExceptions.USER_NOT_EXIST, request.getSenderId());
        }

        /// 1. 将发送的消息转为最新缓存消息存储在redis
        // 获取存储对象
        UserChatLastMessageBo bo = getUserChatLastMessageBo(
                request,
                request.getContent(),
                null,
                senderDo,
                receiverDo,
                MessageTypeEnum.text.code
        );

        // 缓存到Redis  [存储到Redis不属于存储事务；暂时不考虑数据库-缓存数据一致性]
        chatService.saveUserChatMessageToRedis(bo);

        /// 2. 持久化到数据库（异步持久化，oss是在数据库查询不到这条消息的）
        UserChatMessageDo userChatMessageDo = getUserChatMessageDo(
                request,
                request.getContent(),
                MessageTypeEnum.text.code
        );

        /// 3. 存储到服务内存的缓存队列 (降低qps) 也可也换位mq, 毕竟单点不安全, jvm挂掉会发生数据丢失
        chatMessageQueue.addMessage(userChatMessageDo);

        /// 4. socket响应
        UserTextDataResponse response = new UserTextDataResponse();
        response.initResponseByRequest(request);
        // 头像变化自己去查询，不在此处返回

        // netty不提供url
//        List<Long> avatarFileIds = new ArrayList<>();
//        avatarFileIds.add(userDo.getAvatarFileId());
//        List<String> avatarUrls = ossService.getFileUrlsByFileIds(avatarFileIds);
//        response.setAvatarUrls(
//                Optional.ofNullable(avatarUrls)
//                        .filter(u -> !CollectionUtils.isEmpty(u))
//                        .map(u -> u.get(0))
//                        .orElse(null)
//        );

        // 消息推送
        notifySenderAndReceiver(request, senderDo, receiverDo);
    }

    private void notifySenderAndReceiver(@NonNull SendTextDataRequest request, @NotNull UserDo senderDo, @NotNull UserDo receiverDo){
        UserTextDataResponse response = new UserTextDataResponse();
        // 属性设置
        response.initResponseByRequest(request);
        response.setContent(request.getContent());
        response.setSenderName(senderDo.getUserName());

        response.setSenderId(senderDo.getId());
        response.setReceiverId(receiverDo.getId());

        // to receiver
        rabbitMqSender.push(response);

        // to sender
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("androidMessageId", Optional.ofNullable(request.getAndroidMessageId())
                .orElse(String.valueOf(NettyConstants.ERROR_ID))
        );
        NettyUtils.sendSuccessMessage(
                request.getSenderId(),
                dataMap,
                rabbitMqSender
        );
    }

    /**
     * 发送图片
     * 流程：sender以netty将image信息 -> 后端
     * 图片的幂等性：fileName + fileSize + userId
     * 如果幂等，直接通知sender不用上传，直接通知receiver获取资源
     * 生成fileId返回给sender
     * sender调用oss的http方法上传image，此消息中包含messageId
     * @param request   发送图片请求
     */
    @Override
    public void sendImageToUser(SendImageRequest request) {
        // 参数校验
        if (checkParams(request, request.getFileName()) || !request.checkParams()){
            NettyUtils.sendErrorMessage(request.getSenderId(), CommonExceptions.PARAM_ERROR, rabbitMqErrorSender);
            throw new NettyException(CommonExceptions.PARAM_ERROR, request.getSenderId());
        }
        UserDo senderDo = userService.getUserById(request.getSenderId());
        UserDo receiverDo = userService.getUserById(request.getReceiverId());

        if (senderDo == null || receiverDo == null || senderDo.getId() == null || receiverDo.getId() == null){
            NettyUtils.sendErrorMessage(
                    request.getSenderId(),
                    UserExceptions.USER_NOT_EXIST,
                    rabbitMqErrorSender
            );
            // 交给全局异常处理
            throw new NettyException(UserExceptions.USER_NOT_EXIST, request.getSenderId());
        }

        long imageSnowflakeId = IdUtil.getSnowflakeNextId();
        String fileIdStr = String.valueOf(imageSnowflakeId);

        // 获取存储到redis的bo
        UserChatLastMessageBo bo = getUserChatLastMessageBo(
                request,
                request.getContent(),
                fileIdStr,
                senderDo,
                receiverDo,
                MessageTypeEnum.image.code
        );

        // 缓存到Redis
        chatService.saveUserChatMessageToRedis(bo);

        // 获取需要持久化到mongodb的对象
        UserChatMessageDo userChatMessageDo = getUserChatMessageDo(
                request, fileIdStr, MessageTypeEnum.image.code
        );
        // 设置fileId
        userChatMessageDo.setMsgFileId(imageSnowflakeId);

        // 存储到服务内存的缓存队列
//        chatMessageQueue.addMessage(userChatMessageDo);

        // 存储到缓存数据库这条取消：因为此时是还没有上传file，没有获取到file的Id。需要先将消息的信息存储在redis，然后等下oss的时候从redis获取，然后存储在mongodb中
        saveFileMessageToRedis(userChatMessageDo);

        // Socket响应
        // 发送者上传文件
        UploadFileResponse response = new UploadFileResponse();
        // 属性
        response.setFileId(imageSnowflakeId);
        response.setMessageId(request.getAndroidMessageId());
        response.setReceiveMyMessageUserId(receiverDo.getId());
        response.setReceiverAccount(receiverDo.getAccount());
        // id
        response.setSenderId(NettyConstants.SERVER_ID);
        response.setReceiverId(request.getSenderId());
        // type现在上传到oss
        response.setType(ResponseMessageType.Oss.UPLOAD_FILE_NOW);
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));
        // message -> sender
        rabbitMqSender.push(response);
    }

    @Override
    public void deleteAllMessage(DeleteAllMessageRequest request) {
        userChatMessageMongoMapper.deleteAllMessages(
                request.getSenderId(),
                request.getReceiverId()
        );
    }

    /**
     * 获取要记录到Redis的最近消息
     * @param request       请求
     * @param content       消息内容
     * @param msgType       消息类型
     * @return              UserChatLastMessageBo
     * @throws NettyException 入参校验等异常
     */
    // 获得 UserChatLastMessageBo 不用区分发送者和接收者，因为发送者的未读消息数量一定是0
    private UserChatLastMessageBo getUserChatLastMessageBo(
            BaseRequestData request,
            String content,
            String fileIdStr,
            UserDo senderDo,
            UserDo receiverDo,
            int msgType
    ) throws NettyException {
        UserChatLastMessageBo bo = new UserChatLastMessageBo();


        bo.setSenderId(senderDo.getId());
        bo.setReceiverId(receiverDo.getId());
        bo.setSenderAccount(senderDo.getAccount());
        bo.setReceiverAccount(receiverDo.getAccount());

        bo.setMsgContent(content);
        if (StringUtils.hasText(fileIdStr)){
            bo.setFileIdStr(fileIdStr);
        }
        bo.setTimestamp(
                Optional.ofNullable(request.getTimestamp())
                        .map(timeL -> {
                            try {
                                return Long.parseLong(timeL);
                            } catch (Exception e) {
                                return System.currentTimeMillis();
                            }
                        })
                        .orElse(System.currentTimeMillis())
        );
        String name = StringUtils.hasText(senderDo.getUserName()) ? senderDo.getUserName() : senderDo.getAccount();
        bo.setReceiverName(name);

        // 从redis拿数据
        UserChatLastMessageBo currentBo = chatService.getUserChatMessage(request.getSenderId(), request.getReceiverId());
        int unreadCount = currentBo == null ? 0 : currentBo.getUnreadCount();
        unreadCount += 1;
        bo.setUnreadCount(unreadCount);
        bo.setMsgType(msgType);

        return bo;
    }

    // 获取 UserChatMessageDo
    @NotNull
    private UserChatMessageDo getUserChatMessageDo(@NotNull BaseRequestData request, String content, int msgType) {
        long messageId = IdUtil.getSnowflakeNextId();
        UserChatMessageDo messageDo = new UserChatMessageDo();
        // 为message生成id
        messageDo.setId(messageId);
        messageDo.setSenderId(request.getSenderId());
        messageDo.setReceiverId(request.getReceiverId());
        messageDo.setMsgContent(content);
        messageDo.setMsgType(msgType);
        messageDo.setTimestamp(
                Optional.ofNullable(request.getTimestamp())
                        .map(timeStr -> {
                            try {
                                return Long.parseLong(timeStr);
                            } catch (Exception e) {
                                return System.currentTimeMillis();
                            }
                        })
                        .orElse(System.currentTimeMillis())
        );
        return messageDo;
    }

    private void saveFileMessageToRedis(@NonNull UserChatMessageDo messageDo){
        if (messageDo.msgFileId == null){
            log.warn("保存文件消息到Redis失败, 没有fileId::消息内容: [{}]", messageDo);
            return;
        }
        String redisKey = MessageConstant.OSS_FILE_KET +
                messageDo.senderId + ":" + messageDo.receiverId + ":" + messageDo.msgFileId;

        // 存储到redis
        boolean result = redissonService.setObjectByJson(redisKey, messageDo, MessageConstant.OSS_FILE_EXPIRE_TIME);

        if (!result){
            log.warn("保存文件消息到Redis失败::消息内容: [{}]", messageDo);
            throw new NettyException(
                    MessageExceptions.FILE_DATA_STORAGE_FAIL,
                    messageDo.getSenderId()
            );
        }
        else {
            UserChatMessageDo messageDo1 = redissonService.getObjectFromJson(redisKey, UserChatMessageDo.class);
            log.info("已经将文件消息缓存到Redis，redis-key：{}，存储信息：{}", redisKey, messageDo1);
        }
    }


}
