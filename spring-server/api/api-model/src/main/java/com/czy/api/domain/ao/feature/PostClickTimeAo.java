package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/10 11:54
 */
@Data
public class PostClickTimeAo {
    private Long userId;
    private Long postId;
    private Long clickTime;
}
