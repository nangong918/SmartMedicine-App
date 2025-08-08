package com.czy.api.domain.ao;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/8/6 16:19
 * 就是简化版的 UserTempFeatureAo
 * @see com.czy.api.domain.ao.feature.UserTempFeatureAo
 */
@Data
public class UserOnlineFeatureAo {
    // Map<EntityName, NerFeatureScoreAo>
    private Map<String, Double> entityScoreMap = new HashMap<>();

    public interface RedisKey {
        String ENTITY_SCORE_MAP = "entity_score_map:";
    }
}
