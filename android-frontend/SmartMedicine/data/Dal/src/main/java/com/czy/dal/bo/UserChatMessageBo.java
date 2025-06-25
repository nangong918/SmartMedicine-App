package com.czy.dal.bo;


import com.czy.baseUtilsLib.json.BaseBean;

import java.io.Serializable;

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
