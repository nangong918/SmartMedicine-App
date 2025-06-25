package com.czy.dal.dto.netty.request;


import com.czy.dal.dto.http.request.BaseHttpRequest;

public class FetchUserMessageRequest extends BaseHttpRequest {
    public String senderAccount;
    public String receiverAccount;
    // 用于查询消息记录的起始索引
    public Long timestampIndex;
    // 消息条数 当其大于200的时候设置为200
    public Integer messageCount;

    public FetchUserMessageRequest(){

    }
}
