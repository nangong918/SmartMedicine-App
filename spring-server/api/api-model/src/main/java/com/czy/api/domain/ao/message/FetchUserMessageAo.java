package com.czy.api.domain.ao.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/2/26 17:49
 */
@Data
public class FetchUserMessageAo implements Serializable {
    public String senderAccount;
    public String receiverAccount;
    // 用于查询消息记录的起始索引
    public Long timestampIndex;
    // 消息条数 当其大于200的时候设置为200
    public Integer messageCount;
}
