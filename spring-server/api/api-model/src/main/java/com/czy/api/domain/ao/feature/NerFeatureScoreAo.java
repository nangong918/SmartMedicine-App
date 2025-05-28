package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/5 16:25
 * 就是PostNerResult增加Score
 * @see com.czy.api.domain.ao.post.PostNerResult
 */
@Data
public class NerFeatureScoreAo {
    private String keyWord;
    private String nerType;
    private ScoreAo score = new ScoreAo();

    public boolean isEmpty() {
        return keyWord == null || keyWord.isEmpty() || nerType == null || nerType.isEmpty();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        NerFeatureScoreAo cloned = (NerFeatureScoreAo) super.clone();
        // 深拷贝 ScoreAo
        cloned.score = (ScoreAo) score.clone();
        return cloned;
    }
}
