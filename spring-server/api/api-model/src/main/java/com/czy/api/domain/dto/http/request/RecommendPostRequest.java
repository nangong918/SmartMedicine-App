package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/5/24 11:32
 */
@Data
public class RecommendPostRequest {
    @NotNull(message = "用户ID不能为空")
    public Long userId;
    @NotNull(message = "时间戳不能为空")
    public Long timestamp;
}
