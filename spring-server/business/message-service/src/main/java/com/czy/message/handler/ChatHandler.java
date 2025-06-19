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
import com.czy.api.domain.dto.http.response.UploadFileResponse;
import com.czy.api.domain.dto.http.response.UserImageResponse;
import com.czy.api.domain.dto.http.response.UserTextDataResponse;
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
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

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
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        response.setAvatarFileId(userDo.getAvatarFileId());
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
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        Long fileId = ossService.checkFileIdempotentAndBackId(userDo.getId(), request.getFileName(), request.getFileSize());
        // 不存在文件
        if (fileId == null){
            fileId = IdUtil.getSnowflakeNextId();
            String fileIdStr = String.valueOf(fileId);
            // 雪花id（fileId）才是StringContent

            UserChatLastMessageBo bo = getUserChatLastMessageBo(
                    request, fileIdStr, MessageTypeEnum.image.code
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
            UploadFileResponse uploadFileResponse = new UploadFileResponse();
            uploadFileResponse.setFileId(fileId);
            uploadFileResponse.setMessageId(userChatMessageDo.getId());
            uploadFileResponse.setReceiverAccount(request.getReceiverId());
            uploadFileResponse.setSenderId(NettyConstants.SERVER_ID);
            uploadFileResponse.setReceiverId(request.getSenderId());
            // 现在上传到oss
            uploadFileResponse.setType(ResponseMessageType.Oss.UPLOAD_FILE_NOW);
            uploadFileResponse.setTimestamp(String.valueOf(System.currentTimeMillis()));
            // 通知发送者
            rabbitMqSender.push(uploadFileResponse);

            // 等待上传，oss mq通知接收者
            // ChatFile_CONTROLLER/uploadFileSend
        }
        // 存在文件
        else {
            // 直接把id发给接收者
            List<Long> fileIds = Collections.singletonList(fileId);
            String fileIdStr = String.valueOf(fileId);
            List<String> fileUrl = ossService.getFileUrlsByFileIds(fileIds);

            UserChatLastMessageBo bo = getUserChatLastMessageBo(request, fileIdStr, MessageTypeEnum.image.code);

            // 缓存到Redis
            saveUserChatMessageToRedis(bo);

            // 持久化到MySQL
            UserChatMessageDo userChatMessageDo = getUserChatMessageDo(request, fileIdStr, MessageTypeEnum.image.code);
            // 特别单独设置
            userChatMessageDo.setMsgContent(fileIdStr);
            // 存储到服务内存的缓存队列
            chatMessageQueue.addMessage(userChatMessageDo);

            if (!fileUrl.isEmpty()){
                UserImageResponse response = new UserImageResponse();
                response.initResponseByRequest(request);
                response.setImageUrl(fileUrl.get(0));
                response.setTitle(NettyConstants.imageMessageTitle);
                response.setSenderName(userDo.getUserName());
                response.setAvatarFileId(null);
                response.setMessageId(userChatMessageDo.getId());
                // 内部转换
                rabbitMqSender.push(response);
            }
        }


        // 响应在发送者oss上传成功之后发送
//        // Socket响应
//        // 异步mq的socket响应
//        UserImageResponse response = new UserImageResponse();
//        response.initResponseByRequest(request);
//        UserDo userDo = userService.getUserByAccount(request.getSenderId());
//        response.setAvatarFileId(userDo.getAvatarFileId());
//        response.setSenderName(userDo.getUserName());
//        // 图片消息就是图片名称
//        response.setImageUrl(request.fileName);
//        response.setTitle(userDo.getUserName());
//
//        // 消息推送 不用type转换，type转换在pusher中
//        rabbitMqSender.push(response);
    }

    @Override
    public void deleteAllMessage(DeleteAllMessageRequest request) {
        UserDo senderDo = userService.getUserByAccount(request.getSenderId());
        UserDo receiverDo = userService.getUserByAccount(request.getReceiverId());
        userChatMessageMongoMapper.deleteAllMessages(
                senderDo.getId(),
                receiverDo.getId()
        );
    }

    // 获得 UserChatLastMessageBo
    private UserChatLastMessageBo getUserChatLastMessageBo(BaseRequestData request, String content, int msgType) {
        UserChatLastMessageBo userChatLastMessageBo = new UserChatLastMessageBo();
        userChatLastMessageBo.setSenderAccount(request.getSenderId());
        userChatLastMessageBo.setReceiverAccount(request.getReceiverId());
        userChatLastMessageBo.setMsgContent(content);
        userChatLastMessageBo.setTimestamp(Long.parseLong(request.getTimestamp()));
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        String name = StringUtils.hasText(userDo.getUserName()) ? userDo.getUserName() : userDo.getAccount();
        userChatLastMessageBo.setReceiverName(name);
        UserChatLastMessageBo currentBo = chatService.getUserChatMessage(request.getSenderId(), request.getReceiverId());
        int unreadCount = currentBo == null ? 0 : currentBo.unreadCount;
        unreadCount += 1;
        userChatLastMessageBo.setUnreadCount(unreadCount);
        userChatLastMessageBo.setMsgType(msgType);
        return userChatLastMessageBo;
    }

    // 缓存到Redis
    private void saveUserChatMessageToRedis(UserChatLastMessageBo bo) {
        // 缓存到Redis
        UserChatLastMessageBo currentBo = chatService.getUserChatMessage(bo.getSenderAccount(), bo.getReceiverAccount());
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
        Long senderId = userService.getIdByAccount(request.getSenderId());
        Long receiverId = userService.getIdByAccount(request.getReceiverId());
        if (senderId == null || receiverId == null){
            throw new AppException("senderId or receiverId is null");
        }
        userChatMessageDo.setSenderId(senderId);
        userChatMessageDo.setReceiverId(receiverId);
        userChatMessageDo.setMsgContent(content);
        userChatMessageDo.setMsgType(msgType);
        userChatMessageDo.setTimestamp(Long.parseLong(request.getTimestamp()));
        log.info("userChatMessageDo.time:{}", userChatMessageDo.timestamp);
        return userChatMessageDo;
    }


}
