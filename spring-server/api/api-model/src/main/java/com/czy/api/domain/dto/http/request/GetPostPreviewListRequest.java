package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/21 17:26
 */
@Data
public class GetPostPreviewListRequest {
    @NotEmpty(message = "帖子 IDs 不能为空")
    public List<Long> postIds;
    @NotNull(message = "用户Id不能为空")
    public Long userId;
}
