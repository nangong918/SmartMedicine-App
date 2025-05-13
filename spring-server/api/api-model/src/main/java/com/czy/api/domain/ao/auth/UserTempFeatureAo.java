package com.czy.api.domain.ao.auth;

import com.czy.api.domain.ao.feature.NerFeatureScoreAo;
import com.czy.api.domain.ao.feature.ScoreAo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/13 11:48
 *  * 关于特征的处理，clickTime，implicitScore，explicitScore需要配置
 *  * 将全部超参数提取出来方便配置
 */
@Data
public class UserTempFeatureAo {
    // Map<PostId, ScoreAo>
    private Map<Long, ScoreAo> postScoreMap = new HashMap<>();
    // Map<EntityName, NerFeatureScoreAo>
    private Map<String, NerFeatureScoreAo> nerFeatureScoreMap = new HashMap<>();
    // Map<LabelName.code, Score>
    private Map<Integer, ScoreAo> labelScoreMap = new HashMap<>();
}
