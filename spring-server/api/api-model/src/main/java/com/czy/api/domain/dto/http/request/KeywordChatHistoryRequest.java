package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/4/17 9:40
 */
@Data
public class KeywordChatHistoryRequest {
    @NotEmpty(message = "发送者 ID 不能为空")
    public String senderAccount;
    public String receiverAccount;
    @NotEmpty(message = "关键字不能为空")
    public String keyword;
}
