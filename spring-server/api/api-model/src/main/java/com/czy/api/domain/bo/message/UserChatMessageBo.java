package com.czy.api.domain.bo.message;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/2/26 16:55
 * Table:user_chat_message
 * 组合索引（senderId，receiverId）
 */
@Data
public class UserChatMessageBo implements BaseBean, Serializable {
    // 消息id：bigInt/not null/key
    public Long id;
    // 消息内容 text（文本/资源路径）
    public String msgContent;
    // 消息类型（文本、资源） not null
    public Integer msgType;
    // not null;索引
    public String senderAccount;
    // not null;索引
    public String receiverAccount;
    // not null;索引
    public Long senderId;
    //  not null;索引
     public Long receiverId;
    // not null;时间索引；用于找到某个时间节点前后的另一条消息
    // eg：time < timestamp limit 1
    public Long timestamp;
    // 名称
    public String receiverName;
}


