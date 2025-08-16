package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/16 16:35
 */
@Data
public class GetCommentRequest {
    @NotNull(message = "postId不能为空")
    public Long postId;
    @NotNull(message = "pageSize不能为空")
    @Min(value = 0, message = "pageSize不能小于0")
    public Integer pageSize;
    @NotNull(message = "pageNum不能为空")
    @Min(value = 0, message = "pageNum不能小于0")
    public Integer pageNum;
    // 一级评论的id，没有则获取一级评论list；有则获取二级评论list
    public Long level1commentId;
}
