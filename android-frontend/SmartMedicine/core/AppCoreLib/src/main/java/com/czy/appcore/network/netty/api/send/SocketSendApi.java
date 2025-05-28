package com.czy.appcore.network.netty.api.send;




import com.czy.dal.annotation.MessageType;
import com.czy.dal.constant.netty.RequestMessageType;
import com.czy.dal.dto.netty.forwardMessage.SendImageRequest;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.dto.netty.request.DisconnectRequest;
import com.czy.dal.dto.netty.request.HandleAddedUserRequest;
import com.czy.dal.dto.netty.request.HaveReadMessageRequest;
import com.czy.dal.dto.netty.request.PostCollectRequest;
import com.czy.dal.dto.netty.request.PostCommentRequest;
import com.czy.dal.dto.netty.request.PostDisLikeRequest;
import com.czy.dal.dto.netty.request.PostFolderRequest;
import com.czy.dal.dto.netty.request.PostForwardRequest;
import com.czy.dal.dto.netty.request.PostLikeRequest;
import com.czy.dal.dto.netty.request.RegisterRequest;
import com.czy.dal.dto.netty.forwardMessage.SendTextDataRequest;
import com.czy.dal.dto.netty.request.UserBrowseTimeRequest;
import com.czy.dal.dto.netty.request.UserClickPostRequest;

import io.reactivex.annotations.NonNull;

public interface SocketSendApi {

    @MessageType(value = RequestMessageType.Connect.CONNECT, desc = "用户连接")
    void connect(@NonNull RegisterRequest request);

    @MessageType(value = RequestMessageType.Connect.DISCONNECT, desc = "用户断开连接")
    void disconnect(@NonNull DisconnectRequest requestBody);

    @MessageType(value = RequestMessageType.Chat.SEND_TEXT_MESSAGE_TO_USER, desc = "给用户发送资源消息")
    void sendTextToUser(@NonNull SendTextDataRequest request);

    @MessageType(value = RequestMessageType.Chat.SEND_TEXT_MESSAGE_TO_GROUP, desc = "给群组发送文本消息")
    void sendTextToGroup(@NonNull SendTextDataRequest request);

    @MessageType(value = RequestMessageType.Friend.ADD_FRIEND, desc = "添加好友")
    void addFriend(@NonNull AddUserRequest request);

    @MessageType(value = RequestMessageType.Friend.HANDLE_ADDED_USER, desc = "处理添加好友请求")
    void handleAddedUser(@NonNull HandleAddedUserRequest request);

    @MessageType(value = RequestMessageType.ToServer.READ_MESSAGE, desc = "标记消息已读")
    void readMessage(@NonNull HaveReadMessageRequest request);

    @MessageType(value = RequestMessageType.Chat.SEND_IMAGE_MESSAGE_TO_USER, desc = "发送图片消息")
    void sendImageToUser(SendImageRequest request);

    // Post
    @MessageType(value = RequestMessageType.Post.LIKE_POST, desc = "点赞")
    void likePost(@NonNull PostLikeRequest request);

    @MessageType(value = RequestMessageType.Post.COLLECT_POST, desc = "收藏")
    void collectPost(@NonNull PostCollectRequest request);

    @MessageType(value = RequestMessageType.Post.COMMENT_POST, desc = "评论")
    void commentPost(@NonNull PostCommentRequest request);

    @MessageType(value = RequestMessageType.Post.FORWARD_POST, desc = "转发")
    void forwardPost(@NonNull PostForwardRequest request);

    @MessageType(value = RequestMessageType.Post.COLLECT_FOLDER, desc = "收藏夹")
    void collectFolder(@NonNull PostFolderRequest request);

    @MessageType(value = RequestMessageType.Post.NOT_INTERESTED, desc = "不感兴趣")
    void notInterested(@NonNull PostDisLikeRequest request);

    @MessageType(value = RequestMessageType.Logging.LOGGING_CLICK, desc = "点击事件")
    void uploadClickEvent(UserClickPostRequest request);

    @MessageType(value = RequestMessageType.Logging.LOGGING_BROWSE, desc = "浏览事件")
    void uploadBrowseEvent(UserBrowseTimeRequest request);
}
