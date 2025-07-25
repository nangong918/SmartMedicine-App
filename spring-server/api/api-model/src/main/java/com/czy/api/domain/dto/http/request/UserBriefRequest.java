package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/7/24 15:09
 */
@Data
public class UserBriefRequest {
    @NotNull(message = "发送者id不能为空")
    public Long senderId;
    @NotNull(message = "接收者id不能为空")
    public Long receiverId;
    // 从0开始：从第一个开始
    public Integer postNum = 0;
    public Integer postSize = 4;
}
