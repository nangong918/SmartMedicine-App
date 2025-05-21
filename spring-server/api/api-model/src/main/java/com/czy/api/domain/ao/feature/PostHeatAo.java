package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/13 18:28
 */
@Data
public class PostHeatAo {
    private Long postId;
    private Double heatScore = 0.0;
}
