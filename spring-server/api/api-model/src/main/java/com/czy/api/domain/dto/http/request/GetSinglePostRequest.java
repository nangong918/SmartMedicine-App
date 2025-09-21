package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/8 17:27
 */
@Data
public class GetSinglePostRequest {
    @NotNull(message = "帖子ID不能为空")
    public Long postId;
    @NotNull(message = "用户ID不能为空")
    public Long userId;
    @NotNull(message = "页码不能为空")
    @Min(value = 0, message = "获取的评论页数至少从第1页开始")
    public Integer pageNum = 0;
}
