package com.czy.appcore.network.netty.api.send;




import com.czy.dal.annotation.MessageType;
import com.czy.dal.constant.netty.RequestMessageType;
import com.czy.dal.dto.netty.forwardMessage.SendImageRequest;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.dto.netty.request.DeleteAllMessageRequest;
import com.czy.dal.dto.netty.request.DeleteUserRequest;
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
import com.czy.dal.dto.netty.request.UserCityLocationRequest;
import com.czy.dal.dto.netty.request.UserClickPostRequest;

import io.reactivex.annotations.NonNull;

public interface SocketSendApi {

    ///-----------------------ConnectApi-----------------------

    @MessageType(value = RequestMessageType.Connect.CONNECT, desc = "用户连接")
    void connect(@NonNull RegisterRequest request);

    @MessageType(value = RequestMessageType.Connect.DISCONNECT, desc = "用户断开连接")
    void disconnect(@NonNull DisconnectRequest requestBody);

    ///-----------------------ToServiceApi-----------------------

    @MessageType(value = RequestMessageType.ToServer.READ_MESSAGE, desc = "标记消息已读")
    void readMessage(@NonNull HaveReadMessageRequest request);

    ///-----------------------ChatApi-----------------------

    @MessageType(value = RequestMessageType.Chat.SEND_TEXT_MESSAGE_TO_USER, desc = "给用户发送资源消息")
    void sendTextToUser(@NonNull SendTextDataRequest request);

    @Deprecated(since = "暂时取消开发群组功能")
    @MessageType(value = RequestMessageType.Chat.SEND_TEXT_MESSAGE_TO_GROUP, desc = "给群组发送文本消息")
    void sendTextToGroup(@NonNull SendTextDataRequest request);

    @MessageType(value = RequestMessageType.Chat.SEND_IMAGE_MESSAGE_TO_USER, desc = "发送图片消息")
    void sendImageToUser(@NonNull SendImageRequest request);

    @MessageType(value = RequestMessageType.Chat.DELETE_ALL_MESSAGE, desc = "删除用户之间的消息")
    void deleteAllMessage(@NonNull DeleteAllMessageRequest request);

    ///-----------------------FriendApi-----------------------

    @MessageType(value = RequestMessageType.Friend.ADD_FRIEND, desc = "添加好友")
    void addFriend(@NonNull AddUserRequest request);

    @MessageType(value = RequestMessageType.Friend.DELETE_FRIEND, desc = "删除好友的请求")
    void deleteUser(@NonNull DeleteUserRequest request);

    @MessageType(value = RequestMessageType.Friend.HANDLE_ADDED_USER, desc = "处理添加好友请求")
    void handleAddedUser(@NonNull HandleAddedUserRequest request);

    ///-----------------------PostApi-----------------------

    @MessageType(value = RequestMessageType.Post.COLLECT_POST, desc = "收藏帖子")
    void postCollect(@NonNull PostCollectRequest request);

    @MessageType(value = RequestMessageType.Post.COLLECT_FOLDER, desc = "创建收藏夹")
    void collectFolder(@NonNull PostFolderRequest request);

    @MessageType(value = RequestMessageType.Post.COMMENT_POST, desc = "评论帖子")
    void postComment(@NonNull PostCommentRequest request);

    @MessageType(value = RequestMessageType.Post.FORWARD_POST, desc = "转发帖子")
    void postForward(@NonNull PostForwardRequest request);

    @MessageType(value = RequestMessageType.Post.LIKE_POST, desc = "点赞帖子")
    void postLike(@NonNull PostLikeRequest request);

    @MessageType(value = RequestMessageType.Post.NOT_INTERESTED, desc = "不感兴趣")
    void notInterested(@NonNull PostDisLikeRequest request);

    ///-----------------------LoggingApi-----------------------

    @MessageType(value = RequestMessageType.Logging.LOGGING_LOCATION, desc = "城市地区事件")
    void uploadCityLocation(UserCityLocationRequest request);

    @MessageType(value = RequestMessageType.Logging.LOGGING_CLICK, desc = "点击事件")
    void uploadClickEvent(UserClickPostRequest request);

    @MessageType(value = RequestMessageType.Logging.LOGGING_BROWSE, desc = "浏览事件")
    void uploadBrowseEvent(UserBrowseTimeRequest request);

}
