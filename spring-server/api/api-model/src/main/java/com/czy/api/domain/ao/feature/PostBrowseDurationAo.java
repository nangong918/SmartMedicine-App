package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/10 11:53
 */
@Data
public class PostBrowseDurationAo {
    private Long userId;
    private Long postId;
    private Long browseDuration;
    private Double implicitScore;
    private Long timestamp;
}
