package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/10 14:41
 */
@Data
public class UserEntityFeatureAo {
    private Long userId;
    // Map<LabelName, NerFeatureScoreAo>
    private Map<String, NerFeatureScoreAo> nerFeatureScoreMap = new HashMap<>();
    // Map<LabelName, Score>
    private Map<String, Integer> labelScoreMap = new HashMap<>();
}
