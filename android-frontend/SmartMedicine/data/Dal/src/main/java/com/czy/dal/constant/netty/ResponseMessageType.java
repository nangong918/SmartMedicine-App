package com.czy.dal.constant.netty;

/**
 * @author 13225
 * @date 2025/2/11 21:59
 * 服务器返回客户端消息类型：
 *  连接类：
 *      连接成功
 *      断开成功
 *      强制下线
 *  聊天：
 *      收到用户文本消息
 *      收到用户语音消息
 *      收到用户图片消息
 *      收到用户视频消息
 *      收到用户文件消息
 * <p>
 *      收到群组文本消息
 *      收到群组语音消息
 *      收到群组图片消息
 *      收到群组视频消息
 *      收到群组文件消息
 *      收到撤回的消息
 *  实时通讯：
 *      收到用户语音通话
 *      收到用户视频通话
 *      用户语音通话被接受
 *      用户视频通话被接受
 *      用户语音通话被拒绝
 *      用户视频通话被拒绝
 *      用户语音申请被挂断
 *      用户视频申请被挂断
 * <p>
 *      收到群语音通话
 *      收到群视频通话
 *      群语音通话被接受
 *      群视频通话被接受
 *      群语音通话被拒绝
 *      群视频通话被拒绝
 *      群语音申请被挂断
 *      群视频申请被挂断
 *  服务与客户交流：
 *      服务端发送心跳Pong
 *  好友消息：
 *      被删除
 *      被添加好友
 *  朋友圈通知消息：
 *      被点赞
 *      被评论
 *      被收藏
 */
public class ResponseMessageType {
    public static final String responseRoot = "resp:";
    public static final String NULL = "$NULL$";
    public final static class Connect {
        public final static String root = "Connect:";
        public final static String CONNECT_SUCCESS = responseRoot + root + "connectSuccess";
        public final static String DISCONNECT_SUCCESS = responseRoot + root + "disconnectSuccess";
        public final static String FORCE_OFFLINE = responseRoot + root + "forceOffline";
    }

    public final static class Chat {
        public final static String root = "Chat:";
        private static final String fromUser = "fromUser:";
        private static final String fromGroup = "fromGroup:";
        private static final String text = "text";
        private static final String voice = "voice";
        private static final String image = "image";
        private static final String video = "video";
        private static final String file = "file";
        public final static String RECEIVE_USER_TEXT_MESSAGE = responseRoot + root + fromUser + text;
        public final static String RECEIVE_USER_VOICE_MESSAGE = responseRoot + root + fromUser + voice;
        public final static String RECEIVE_USER_IMAGE_MESSAGE = responseRoot + root + fromUser + image;
        public final static String RECEIVE_USER_VIDEO_MESSAGE = responseRoot + root + fromUser + video;
        public final static String RECEIVE_USER_FILE_MESSAGE = responseRoot + root + fromUser + file;
        public final static String RECEIVE_GROUP_TEXT_MESSAGE = responseRoot + root + fromGroup + text;
        public final static String RECEIVE_GROUP_VOICE_MESSAGE = responseRoot + root + fromGroup + voice;
        public final static String RECEIVE_GROUP_IMAGE_MESSAGE = responseRoot + root + fromGroup + image;
        public final static String RECEIVE_GROUP_VIDEO_MESSAGE = responseRoot + root + fromGroup + video;
        public final static String RECEIVE_GROUP_FILE_MESSAGE = responseRoot + root + fromGroup + file;
        public final static String RECALL_MESSAGE = responseRoot + root + "recallMessage";
        // 消息已读
        public final static String MESSAGE_HAVE_BEEN_READ = responseRoot + root + "messageHaveBeenRead";
    }

    public final static class ToServer {
        public final static String root = "ToServer:";
        public final static String PONG = responseRoot + root + "pong";
    }

    public final static class Friend {
        public final static String root = "Friend:";
        public final static String DELETED_FRIEND = responseRoot + root + "deletedFriend";
        public final static String ADDED_FRIEND = responseRoot + root + "addedFriend";
        public static final String HANDLE_ADDED_USER = responseRoot + root + "handleAddedUser";
    }

    public final static class Post {
        public final static String root = "Post:";
        // 朋友圈被点赞
        public final static String LIKE_POST = responseRoot + root + "likePost";
        // 朋友圈被评论
        public final static String COMMENT_POST = responseRoot + root + "commentPost";
        // 朋友圈被收藏
        public final static String COLLECT_POST = responseRoot + root + "collectPost";
        public static final String FORWARD_POST = responseRoot + root + "forwardPost";
        public static final String COLLECT_FOLDER = responseRoot + root + "folder";
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
        // 收到用户语音通话
        public final static String RECEIVE_USER_VOICE_CALL = responseRoot + root + user + received + voice;
        // 收到用户视频通话
        public final static String RECEIVE_USER_VIDEO_CALL = responseRoot + root + user + received + video;
        // 用户语音通话被接受
        public final static String USER_VOICE_CALL_ACCEPT = responseRoot + root + user + accept + voice;
        // 用户视频通话被接受
        public final static String USER_VIDEO_CALL_ACCEPT = responseRoot + root + user + accept + video;
        // 用户语音通话被拒绝
        public final static String USER_VOICE_CALL_REJECT = responseRoot + root + user + reject + voice;
        // 用户视频通话被拒绝
        public final static String USER_VIDEO_CALL_REJECT = responseRoot + root + user + reject + video;
        // 用户语音通话被挂断
        public final static String USER_VOICE_CALL_HANGUP = responseRoot + root + user + hangup + voice;
        // 用户视频通话被挂断
        public final static String USER_VIDEO_CALL_HANGUP = responseRoot + root + user + hangup + video;
        // 收到群语音通话
        public final static String RECEIVE_GROUP_VOICE_CALL = responseRoot + root + group + received + voice;
        // 收到群视频通话
        public final static String RECEIVE_GROUP_VIDEO_CALL = responseRoot + root + group + received + video;
        // 群语音通话被接受
        public final static String GROUP_VOICE_CALL_ACCEPT = responseRoot + root + group + accept + voice;
        // 群视频通话被接受
        public final static String GROUP_VIDEO_CALL_ACCEPT = responseRoot + root + group + accept + video;
        // 群语音通话被拒绝
        public final static String GROUP_VOICE_CALL_REJECT = responseRoot + root + group + reject + voice;
        // 群视频通话被拒绝
        public final static String GROUP_VIDEO_CALL_REJECT = responseRoot + root + group + reject + video;
        // 群语音通话被挂断
        public final static String GROUP_VOICE_CALL_HANGUP = responseRoot + root + group + hangup + voice;
        // 群视频通话被挂断
        public final static String GROUP_VIDEO_CALL_HANGUP = responseRoot + root + group + hangup + video;
    }

    public final static class Oss {
        public final static String root = "Oss:";
        // 要求现在上传文件
        public final static String UPLOAD_FILE_NOW = responseRoot + root + "uploadFileNow";
        // 上传结果
        public final static String UPLOAD_FILE = responseRoot + root + "uploadFile";
        // 删除结果
        public final static String DELETE_FILE = responseRoot + root + "deleteFile";
    }

}
