package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/5/13 18:28
 */
@Data
public class PostHeatAo implements Serializable {
    private Long postId;
    private Double heatScore = 0.0;
}
