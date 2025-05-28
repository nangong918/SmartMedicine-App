package com.czy.dal.constant.netty;


import com.czy.dal.annotation.MsgTranslator;

/**
 * @author 13225
 * @date 2025/2/11 21:26
 * 客户端消息类型：
 *  连接类：
 *      连接
 *      断开
 *  聊天：
 *      发送文本消息给用户
 *      发送语音消息给用户
 *      发送图片消息给用户
 *      发送视频消息给用户
 *      发送文件消息给用户
 * <p>
 *      发送文本消息给群组
 *      发送语音消息给群组
 *      发送图片消息给群组
 *      发送视频消息给群组
 *      发送文件消息给群组
 *      撤回消息
 *  实时通讯：
 *      发送用户语音通话邀请
 *      发送用户视频通话邀请
 *      接受用户语音通话邀请
 *      接受用户视频通话邀请
 *      拒绝用户通话邀请
 *      拒绝用户视频邀请
 *      挂断用户语音通话
 *      挂断用户视频通话
 * <p>
 *      发送群语音通话邀请
 *      发送群视频通话邀请
 *      接受群语音通话邀请
 *      接受群视频通话邀请
 *      拒绝群通话邀请
 *      拒绝群视频邀请
 *      挂断群语音通话
 *      挂断群视频通话
 *  服务与客户交流：
 *      客户端发送的心跳Ping
 *  好友间消息：
 *      添加好友
 *      删除好友
 *  朋友圈通知消息：
 *      发出动态
 *      删除动态
 *      点赞
 *      评论
 *      收藏
 */
public class RequestMessageType {

    public final static String requestRoot = "req:";
    public final static class Connect {
        public final static String root = "Connect:";

        // 连接
        @MsgTranslator(responseType = ResponseMessageType.Connect.CONNECT_SUCCESS)
        public final static String CONNECT = requestRoot + root + "connect";
        // 断开连接
        @MsgTranslator(responseType = ResponseMessageType.Connect.DISCONNECT_SUCCESS)
        public final static String DISCONNECT = requestRoot + root + "disconnect";
    }
    public final static class ToServer {
        public final static String root = "ToServer:";

        // 客户端发送的心跳Ping
        @MsgTranslator(responseType = ResponseMessageType.ToServer.PONG)
        public final static String PING = requestRoot + root + "ping";
        // 已读 [此处是跨类型；因为Message读了之后 -> 后端 -> 客户端；也就是说服务端和客户端都需要知道]
        @MsgTranslator(responseType = ResponseMessageType.Chat.MESSAGE_HAVE_BEEN_READ)
        public final static String READ_MESSAGE = requestRoot + root + "readMessage";
    }
    public final static class Chat {
        public final static String root = "Chat:";
        private static final String toUser = "toUser:";
        private static final String toGroup = "toGroup:";
        private static final String text = "text";
        private static final String voice = "voice";
        private static final String image = "image";
        private static final String video = "video";
        private static final String file = "file";
        // 发送文本消息给用户
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_USER_TEXT_MESSAGE)
        public final static String SEND_TEXT_MESSAGE_TO_USER = requestRoot + root + toUser + text;
        // 发送语音消息给用户
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_USER_VOICE_MESSAGE)
        public final static String SEND_VOICE_MESSAGE_TO_USER = requestRoot + root + toUser + voice;
        // 发送图片消息给用户
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_USER_IMAGE_MESSAGE)
        public final static String SEND_IMAGE_MESSAGE_TO_USER = requestRoot + root + toUser + image;
        // 发送视频消息给用户
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_USER_VIDEO_MESSAGE)
        public final static String SEND_VIDEO_MESSAGE_TO_USER = requestRoot + root + toUser + video;
        // 发送文件消息给用户
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_USER_FILE_MESSAGE)
        public final static String SEND_FILE_MESSAGE_TO_USER = requestRoot + root + toUser + file;
        // 发送文本消息给群组
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_GROUP_TEXT_MESSAGE)
        public final static String SEND_TEXT_MESSAGE_TO_GROUP = requestRoot + root + toGroup + text;
        // 发送语音消息给群组
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_GROUP_VOICE_MESSAGE)
        public final static String SEND_VOICE_MESSAGE_TO_GROUP = requestRoot + root + toGroup + voice;
        // 发送图片消息给群组
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_GROUP_IMAGE_MESSAGE)
        public final static String SEND_IMAGE_MESSAGE_TO_GROUP = requestRoot + root + toGroup + image;
        // 发送视频消息给群组
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_GROUP_VIDEO_MESSAGE)
        public final static String SEND_VIDEO_MESSAGE_TO_GROUP = requestRoot + root + toGroup + video;
        // 发送文件消息给群组
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECEIVE_GROUP_FILE_MESSAGE)
        public final static String SEND_FILE_MESSAGE_TO_GROUP = requestRoot + root + toGroup + file;
        // 撤回消息
        @MsgTranslator(responseType = ResponseMessageType.Chat.RECALL_MESSAGE)
        public final static String RECALL_MESSAGE = requestRoot + root + "recallMessage";
    }
    public final static class Friend {
        public final static String root = "Friend:";
        private static final String add = "add";
        private static final String delete = "delete";
        private static final String handle = "handle";
        // 添加好友 ADD_FRIEND -> ADDED_FRIEND ; HANDLE_ADDED_USER -> HANDLE_ADDED_USER
        @MsgTranslator(responseType = ResponseMessageType.Friend.ADDED_FRIEND)
        public final static String ADD_FRIEND = requestRoot + root + add;
        // 删除好友
        @MsgTranslator(responseType = ResponseMessageType.Friend.DELETED_FRIEND)
        public final static String DELETE_FRIEND = requestRoot + root + delete;
        // 处理添加好友请求
        @MsgTranslator(responseType = ResponseMessageType.Friend.HANDLE_ADDED_USER)
        public static final String HANDLE_ADDED_USER = requestRoot + root + handle;
    }

    public final static class Post {
        public final static String root = "Post:";
        private static final String like = "like";
        private static final String comment = "comment";
        private static final String collect = "collect";
        // 不感兴趣
        private static final String notInterested = "notInterested";
        // 转发
        private static final String forward = "forward";
        // 文件夹
        private static final String folder = "folder";

        // 点赞
        @MsgTranslator(responseType = ResponseMessageType.Post.LIKE_POST)
        public final static String LIKE_POST = requestRoot + root + like;
        // 评论
        @MsgTranslator(responseType = ResponseMessageType.Post.COMMENT_POST)
        public final static String COMMENT_POST = requestRoot + root + comment;
        // 收藏
        @MsgTranslator(responseType = ResponseMessageType.Post.COLLECT_POST)
        public final static String COLLECT_POST = requestRoot + root + collect;
        // 转发
        @MsgTranslator(responseType = ResponseMessageType.Post.FORWARD_POST)
        public final static String FORWARD_POST = requestRoot + root + forward;
        // 收藏文件夹
        @MsgTranslator(responseType = ResponseMessageType.Post.COLLECT_FOLDER)
        public final static String COLLECT_FOLDER = requestRoot + root + folder;
        // 不感兴趣
        @MsgTranslator(responseType = ResponseMessageType.NULL)
        public final static String NOT_INTERESTED = requestRoot + root + notInterested;
    }

    public final static class Call {
        public final static String root = "Call:";
        // p2p or group
        private static final String user = "user:";
        private static final String group = "group:";
        // call type
        private static final String voice = "voice";
        private static final String video = "video";
        // option
        private static final String send = "send:";
        private static final String received = "received:";
        private static final String accept = "accept:";
        private static final String reject = "reject:";
        private static final String hangup = "hangup:";
        // 发送用户语音通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.RECEIVE_USER_VOICE_CALL)
        public final static String SEND_USER_VOICE_CALL_INVITE = requestRoot + root + user + send + voice;
        // 发送用户视频通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.RECEIVE_USER_VIDEO_CALL)
        public final static String SEND_USER_VIDEO_CALL_INVITE = requestRoot + root + user + send + video;
        // 接受用户语音通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.USER_VOICE_CALL_ACCEPT)
        public final static String ACCEPT_USER_VOICE_CALL_INVITE = requestRoot + root + user + accept + voice;
        // 接受用户视频通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.USER_VIDEO_CALL_ACCEPT)
        public final static String ACCEPT_USER_VIDEO_CALL_INVITE = requestRoot + root + user + accept + video;
        // 拒绝用户语音通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.USER_VOICE_CALL_REJECT)
        public final static String REJECT_USER_VOICE_CALL_INVITE = requestRoot + root + user + reject + voice;
        // 拒绝用户视频通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.USER_VIDEO_CALL_REJECT)
        public final static String REJECT_USER_VIDEO_CALL_INVITE = requestRoot + root + user + reject + video;
        // 挂断用户语音通话
        @MsgTranslator(responseType = ResponseMessageType.Call.USER_VOICE_CALL_HANGUP)
        public final static String HANGUP_USER_VOICE_CALL = requestRoot + root + user + hangup + voice;
        // 挂断用户视频通话
        @MsgTranslator(responseType = ResponseMessageType.Call.USER_VIDEO_CALL_HANGUP)
        public final static String HANGUP_USER_VIDEO_CALL = requestRoot + root + user + hangup + video;
        // 发送群组语音通话邀请
        public final static String SEND_GROUP_VOICE_CALL_INVITE = requestRoot + root + group + send + voice;
        // 发送群组视频通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.RECEIVE_GROUP_VIDEO_CALL)
        public final static String SEND_GROUP_VIDEO_CALL_INVITE = requestRoot + root + group + send + video;
        // 接受群组语音通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.GROUP_VOICE_CALL_ACCEPT)
        public final static String ACCEPT_GROUP_VOICE_CALL_INVITE = requestRoot + root + group + accept + voice;
        // 接受群组视频通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.GROUP_VIDEO_CALL_ACCEPT)
        public final static String ACCEPT_GROUP_VIDEO_CALL_INVITE = requestRoot + root + group + accept + video;
        // 拒绝群组语音通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.GROUP_VOICE_CALL_REJECT)
        public final static String REJECT_GROUP_VOICE_CALL_INVITE = requestRoot + root + group + reject + voice;
        // 拒绝群组视频通话邀请
        @MsgTranslator(responseType = ResponseMessageType.Call.GROUP_VIDEO_CALL_REJECT)
        public final static String REJECT_GROUP_VIDEO_CALL_INVITE = requestRoot + root + group + reject + video;
        // 挂断群组语音通话
        @MsgTranslator(responseType = ResponseMessageType.Call.GROUP_VOICE_CALL_HANGUP)
        public final static String HANGUP_GROUP_VOICE_CALL = requestRoot + root + group + hangup + voice;
        // 挂断群组视频通话
        @MsgTranslator(responseType = ResponseMessageType.Call.GROUP_VIDEO_CALL_HANGUP)
        public final static String HANGUP_GROUP_VIDEO_CALL = requestRoot + root + group + hangup + video;
        // 对方正方
    }

    public final static class Oss {
        public final static String root = "Oss:";
        @MsgTranslator(responseType = ResponseMessageType.Oss.UPLOAD_FILE)
        public final static String UPLOAD_FILE = requestRoot + root + "uploadFile";
        @MsgTranslator(responseType = ResponseMessageType.Oss.DELETE_FILE)
        public final static String DELETE_FILE = requestRoot + root + "deleteFile";
    }

    public final static class Logging {
        public final static String root = "Logging:";
        // 位置信息事件
        private static final String location = "location:";
        // 点击事件
        private static final String click = "click:";
        // 浏览事件
        private static final String browse = "browse:";

        @MsgTranslator(responseType = ResponseMessageType.NULL)
        public final static String LOGGING_LOCATION = requestRoot + root + location;
        @MsgTranslator(responseType = ResponseMessageType.NULL)
        public final static String LOGGING_CLICK = requestRoot + root + click;
        @MsgTranslator(responseType = ResponseMessageType.NULL)
        public final static String LOGGING_BROWSE = requestRoot + root + browse;
    }
}
