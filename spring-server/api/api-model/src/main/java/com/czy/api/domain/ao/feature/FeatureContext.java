package com.czy.api.domain.ao.feature;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 17:01
 */
@Data
public class FeatureContext {
    // 用户 ID
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;
    // 当前环境感兴趣的帖子
    @NotEmpty(message = "帖子 IDs 不能为空")
    private List<Long> postIds;
    // timestamp
    @NotEmpty(message = "时间戳不能为空")
    private Long timestamp;
}
