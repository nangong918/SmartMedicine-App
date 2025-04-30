package com.czy.api.constant.message;

/**
 * @author 13225
 * @date 2025/3/12 17:17
 */
public class MessageConstant {

    // 最近的消息Key [包含的是UserChatLastMessageBo]
    public static final String CHAT_MESSAGE_KEY = "chat:last_message:";
    public static final String serviceName = "message-service";
    public static final String serviceRoute = "/" + serviceName;
    public static final String Chat_CONTROLLER = "/chat";
    public static final String ChatFile_CONTROLLER = "/chatFile";
    public static final String WebRTC_CONTROLLER = "/webrtc";
    public static final String serviceUri = "lb://" + serviceName;
}
