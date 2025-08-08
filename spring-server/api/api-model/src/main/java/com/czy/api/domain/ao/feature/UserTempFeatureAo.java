package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/13 11:48
 *  * 关于特征的处理，clickTime，implicitScore，explicitScore需要配置
 *  * 将全部超参数提取出来方便配置
 * @see com.czy.api.domain.ao.UserOnlineFeatureAo
 */
@Data
public class UserTempFeatureAo {
    // Map<PostId, ScoreAo>
    private Map<Long, ScoreAo> postScoreMap = new HashMap<>();
    // Map<EntityName, NerFeatureScoreAo>
    private Map<String, NerFeatureScoreAo> nerFeatureScoreMap = new HashMap<>();
    // Map<LabelName.code, Score>
    private Map<Integer, ScoreAo> labelScoreMap = new HashMap<>();

    public boolean isEmpty() {
        return postScoreMap.isEmpty() && nerFeatureScoreMap.isEmpty() && labelScoreMap.isEmpty();
    }

    public interface RedisKey {
        String POST_SCORE_MAP = "post_score_map:";
        String NER_FEATURE_SCORE_MAP = "ner_feature_score_map:";
        String LABEL_SCORE_MAP = "label_score_map:";
    }
}
