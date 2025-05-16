package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 17:01
 */
@Data
public class FeatureContext {
    private Long userId;
    // 当前环境感兴趣的帖子
    private List<Long> postIds;
}
