package com.czy.dal.Do;


/**
 * @author 13225
 * @date 2025/2/26 16:55
 * Table:user_chat_message
 * 组合索引（senderId，receiverId）
 */
public class UserChatMessageDo {
    // bigInt/not null/key
    public Long id;
    // text
    public String msgContent;
    // not null
    public Integer msgType;
    // not null;索引
    public Integer senderId;
    // not null;索引
    public Integer receiverId;
    // not null;时间索引；用于找到某个时间节点前后的另一条消息
    // eg：time < timestamp limit 1
    public Long timestamp;
}
