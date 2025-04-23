package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.dto.http.base.BaseNettyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/4/21 11:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostUpdateRequest extends BaseNettyRequest {
    // title；not null
    @NotNull(message = "标题不能为空")
    public String title;
    // content；not null
    @NotNull(message = "内容不能为空")
    public String content;
    @NotNull(message = "帖子 ID 不能为空")
    public Long postId;
    @NotNull(message = "是否包含文件判断值不能为空")
    public Boolean isHaveFiles;
}
