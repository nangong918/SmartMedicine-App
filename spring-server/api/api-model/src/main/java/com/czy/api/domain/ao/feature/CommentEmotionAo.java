package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/21 17:21
 */
@Data
public class CommentEmotionAo {
    private Integer commentEmotionType;
    private Double confidenceLevel;
}
