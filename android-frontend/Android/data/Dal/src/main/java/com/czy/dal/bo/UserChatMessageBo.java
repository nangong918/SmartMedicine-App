package com.czy.dal.bo;


import com.czy.baseUtilsLib.json.BaseBean;

public class UserChatMessageBo implements BaseBean {
    // bigInt/not null/key
    public Long id;
    // text
    public String msgContent;
    // not null
    public Integer msgType;
    // not null;索引
    public String senderAccount;
    // not null;索引
    public String receiverAccount;
    // not null;时间索引；用于找到某个时间节点前后的另一条消息
    // eg：time < timestamp limit 1
    public Long timestamp;
    // 名称
    public String receiverName;
}
