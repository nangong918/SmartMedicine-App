package com.czy.api.domain.dto.http.request;


import com.czy.api.domain.dto.http.base.BaseNettyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/26 14:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FetchUserMessageRequest extends BaseNettyRequest {
    public String senderAccount;
    public String receiverAccount;
    // 用于查询消息记录的起始索引
    public Long timestampIndex;
    // 消息条数 当其大于200的时候设置为200
    public Integer messageCount;
}
