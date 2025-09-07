package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/5/21 17:21
 */
@Data
public class CommentEmotionAo implements Serializable {
    private Integer commentEmotionType;
    private Double confidenceLevel;
}
