package com.czy.message.handler;


import cn.hutool.core.util.IdUtil;
import com.czy.api.api.message.ChatService;
import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.MessageTypeEnum;
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
import com.czy.message.handler.api.ChatApi;
import com.czy.message.mapper.mongo.UserChatMessageMongoMapper;
import com.czy.message.mq.sender.RabbitMqSender;
import com.czy.message.queue.ChatMessageQueue;
import com.czy.springUtils.annotation.HandlerType;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final RabbitMqSender rabbitMqSender;
    private final ChatService chatService;
    private final ChatMessageQueue chatMessageQueue;
    private final UserChatMessageMongoMapper userChatMessageMongoMapper;
    @Override
    public void sendTextMessageToUser(SendTextDataRequest request) {
        // 将发送的消息转为最新缓存消息存储在redis
        UserChatLastMessageBo bo = getUserChatLastMessageBo(request, request.getContent(), MessageTypeEnum.text.code);

        // 缓存到Redis  [存储到Redis不属于存储事务；暂时不考虑数据库-缓存数据一致性]
        saveUserChatMessageToRedis(bo);

        // 持久化到数据库（异步持久化，oss是在数据库查询不到这条消息的）
        UserChatMessageDo userChatMessageDo = getUserChatMessageDo(request, request.getContent(), MessageTypeEnum.text.code);
        // 存储到服务内存的缓存队列
        chatMessageQueue.addMessage(userChatMessageDo);

        // socket响应
        UserTextDataResponse response = new UserTextDataResponse();
        response.initResponseByRequest(request);
        // 头像变化自己去查询，不在此处返回

        UserDo userDo = userService.getUserById(request.getSenderId());

        List<Long> avatarFileIds = new ArrayList<>();
        avatarFileIds.add(userDo.getAvatarFileId());
        List<String> avatarUrls = ossService.getFileUrlsByFileIds(avatarFileIds);
        response.setAvatarUrls(
                Optional.ofNullable(avatarUrls)
                        .filter(u -> !CollectionUtils.isEmpty(u))
                        .map(u -> u.get(0))
                        .orElse(null)
        );

        String name = StringUtils.hasText(userDo.getUserName()) ? userDo.getUserName() : userDo.getAccount();
        response.setSenderName(name);
        response.setContent(request.getContent());
        // 文本消息就是名称（什么备注不备注的，系统别搞那么复杂）
        response.setTitle(name);

        // 消息推送 不用type转换，type转换在pusher中
        rabbitMqSender.push(response);
    }

    /**
     * 发送图片
     * 流程：sender以netty将image信息 -> 后端
     * 图片的幂等性：fileName + fileSize + userId
     * 如果幂等，直接通知sender不用上传，直接通知receiver获取资源
     * 生成fileId返回给sender
     * sender调用oss的http方法上传image，此消息中包含messageId
     * @param request
     */
    @Override
    public void sendImageToUser(SendImageRequest request) {
        // file的幂等性（senderAccount，fileName，fileSize）
        if (!StringUtils.hasText(request.getFileName()) || request.getFileSize() <= 0){
            throw new AppException("fileName or fileSize is null");
        }
        if (!userService.checkUserExist(request.getSenderId())){
            throw new AppException("user not exist");
        }

        long imageSnowflakeId = IdUtil.getSnowflakeNextId();
        String fileIdStr = String.valueOf(imageSnowflakeId);

        UserChatLastMessageBo bo = getUserChatLastMessageBo(
                request,
                fileIdStr,
                MessageTypeEnum.image.code
        );

        // 缓存到Redis
        saveUserChatMessageToRedis(bo);

        // 持久化到MySQL
        UserChatMessageDo userChatMessageDo = getUserChatMessageDo(
                request, fileIdStr, MessageTypeEnum.image.code
        );

        // 特别单独设置
        userChatMessageDo.setMsgContent(fileIdStr);
        // 存储到服务内存的缓存队列
        chatMessageQueue.addMessage(userChatMessageDo);

        // Socket响应
        // 发送者上传文件
        UserDo receiverDo = userService.getUserById(request.getReceiverId());
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setFileId(imageSnowflakeId);
        uploadFileResponse.setMessageId(userChatMessageDo.getId());
        uploadFileResponse.setReceiverAccount(receiverDo.getAccount());
        uploadFileResponse.setSenderId(NettyConstants.SERVER_ID);
        uploadFileResponse.setReceiverId(request.getSenderId());
        // 现在上传到oss
        uploadFileResponse.setType(ResponseMessageType.Oss.UPLOAD_FILE_NOW);
        uploadFileResponse.setTimestamp(String.valueOf(System.currentTimeMillis()));
        // 通知sender上传文件，上传成功之后再通知receiver
        rabbitMqSender.push(uploadFileResponse);
    }

    @Override
    public void deleteAllMessage(DeleteAllMessageRequest request) {
        userChatMessageMongoMapper.deleteAllMessages(
                request.getSenderId(),
                request.getReceiverId()
        );
    }

    // 获得 UserChatLastMessageBo 不用区分发送者和接收者，因为发送者的未读消息数量一定是0
    private UserChatLastMessageBo getUserChatLastMessageBo(BaseRequestData request, String content, int msgType) {
        UserChatLastMessageBo bo = new UserChatLastMessageBo();
        UserDo senderDo = userService.getUserById(request.getSenderId());
        UserDo receiverDo = userService.getUserById(request.getReceiverId());

        bo.setSenderId(senderDo.getId());
        bo.setReceiverId(receiverDo.getId());
        bo.setSenderAccount(senderDo.getAccount());
        bo.setReceiverAccount(receiverDo.getAccount());

        bo.setMsgContent(content);
        bo.setTimestamp(Long.parseLong(request.getTimestamp()));
        String name = StringUtils.hasText(senderDo.getUserName()) ? senderDo.getUserName() : senderDo.getAccount();
        bo.setReceiverName(name);

        // 从redis拿数据
        UserChatLastMessageBo currentBo = chatService.getUserChatMessage(request.getSenderId(), request.getReceiverId());
        int unreadCount = currentBo == null ? 0 : currentBo.unreadCount;
        unreadCount += 1;
        bo.setUnreadCount(unreadCount);
        bo.setMsgType(msgType);
        return bo;
    }

    // 缓存到Redis
    private void saveUserChatMessageToRedis(UserChatLastMessageBo bo) {
        // 缓存到Redis
        UserChatLastMessageBo currentBo = chatService.getUserChatMessage(bo.getSenderId(), bo.getReceiverId());
        int unreadCount = currentBo == null ? 0 : currentBo.unreadCount;
        unreadCount += 1;
        bo.setUnreadCount(unreadCount);
        chatService.saveUserChatMessageToRedis(bo);
    }

    // 获取 UserChatMessageDo
    private UserChatMessageDo getUserChatMessageDo(BaseRequestData request, String content, int msgType) {
        long messageId = IdUtil.getSnowflakeNextId();
        UserChatMessageDo userChatMessageDo = new UserChatMessageDo();
        // 为message生成id
        userChatMessageDo.setId(messageId);
        userChatMessageDo.setSenderId(request.getSenderId());
        userChatMessageDo.setReceiverId(request.getReceiverId());
        userChatMessageDo.setMsgContent(content);
        userChatMessageDo.setMsgType(msgType);
        userChatMessageDo.setTimestamp(Long.parseLong(request.getTimestamp()));
        log.info("userChatMessageDo.time:{}", userChatMessageDo.timestamp);
        return userChatMessageDo;
    }


}
