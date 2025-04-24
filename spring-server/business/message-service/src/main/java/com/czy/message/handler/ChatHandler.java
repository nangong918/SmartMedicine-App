package com.czy.message.handler;


import com.czy.api.api.user.UserService;
import com.czy.api.constant.MessageTypeEnum;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.bo.message.UserChatLastMessageBo;
import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.dto.http.request.SendImageRequest;
import com.czy.api.domain.dto.http.request.SendTextDataRequest;
import com.czy.api.domain.dto.http.response.UserImageResponse;
import com.czy.api.domain.dto.http.response.UserTextDataResponse;
import com.czy.springUtils.annotation.HandlerType;
import com.czy.message.component.RabbitMqSender;
import com.czy.message.handler.api.ChatApi;
import com.czy.api.api.message.ChatService;
import com.czy.message.queue.ChatMessageQueue;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    private final RabbitMqSender rabbitMqSender;
    private final ChatService chatService;
    private final ChatMessageQueue chatMessageQueue;
    @Override
    public void sendTextMessageToUser(SendTextDataRequest request) {
        UserChatLastMessageBo bo = getUserChatLastMessageBo(request, request.getContent(), MessageTypeEnum.text.code);

        // 缓存到Redis  [存储到Redis不属于存储事务；暂时不考虑数据库-缓存数据一致性]
        saveUserChatMessageToRedis(bo);

        // 持久化到MySQL
        UserChatMessageDo userChatMessageDo = getUserChatMessageDo(request, request.getContent(), MessageTypeEnum.text.code);
        // 存储到服务内存的缓存队列
        chatMessageQueue.addMessage(userChatMessageDo);

        // socket响应
        UserTextDataResponse response = new UserTextDataResponse();
        response.initResponseByRequest(request);
        // ??头像是否变化??
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        response.setAvatarUrl(userDo.getAvatarFileId());
        String name = StringUtils.hasText(userDo.getUserName()) ? userDo.getUserName() : userDo.getAccount();
        response.setSenderName(name);
        response.setContent(request.getContent());
        // 文本消息就是名称（什么备注不备注的，系统别搞那么复杂）
        response.setTitle(name);

        // 消息推送 不用type转换，type转换在pusher中
        rabbitMqSender.push(response);
    }

    @Override
    public void sendImageToUser(SendImageRequest request) {
        UserChatLastMessageBo bo = getUserChatLastMessageBo(request, request.fileName, MessageTypeEnum.image.code);

        // 缓存到Redis
        saveUserChatMessageToRedis(bo);

        // 持久化到MySQL
        UserChatMessageDo userChatMessageDo = getUserChatMessageDo(request, request.fileName, MessageTypeEnum.image.code);
        // 特别单独设置
        userChatMessageDo.msgContent = request.fileName;
        // 存储到服务内存的缓存队列
        chatMessageQueue.addMessage(userChatMessageDo);

        // Socket响应
        UserImageResponse response = new UserImageResponse();
        response.initResponseByRequest(request);
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        response.setAvatarUrl(userDo.getAvatarFileId());
        response.setSenderName(userDo.getUserName());
        // 图片消息就是图片名称
        response.setImageUrl(request.fileName);
        response.setTitle(userDo.getUserName());

        // 消息推送 不用type转换，type转换在pusher中
        rabbitMqSender.push(response);
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
        UserChatMessageDo userChatMessageDo = new UserChatMessageDo();
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
