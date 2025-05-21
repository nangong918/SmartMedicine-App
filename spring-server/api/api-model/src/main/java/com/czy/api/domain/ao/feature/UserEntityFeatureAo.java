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
    // Map<EntityName, NerFeatureScoreAo>
    private Map<String, NerFeatureScoreAo> nerFeatureScoreMap = new HashMap<>();
    // Map<LabelName.code, Score>
    private Map<Integer, ScoreAo> labelScoreMap = new HashMap<>();

    @Override
    public Object clone() throws CloneNotSupportedException {
        UserEntityFeatureAo cloned = (UserEntityFeatureAo) super.clone();

        // 深拷贝 nerFeatureScoreMap
        cloned.nerFeatureScoreMap = new HashMap<>();
        for (Map.Entry<String, NerFeatureScoreAo> entry : this.nerFeatureScoreMap.entrySet()) {
            cloned.nerFeatureScoreMap.put(entry.getKey(), (NerFeatureScoreAo) entry.getValue().clone());
        }

        // 深拷贝 labelScoreMap
        cloned.labelScoreMap = new HashMap<>();
        for (Map.Entry<Integer, ScoreAo> entry : this.labelScoreMap.entrySet()) {
            cloned.labelScoreMap.put(entry.getKey(), (ScoreAo) entry.getValue().clone());
        }

        return cloned;
    }
}
