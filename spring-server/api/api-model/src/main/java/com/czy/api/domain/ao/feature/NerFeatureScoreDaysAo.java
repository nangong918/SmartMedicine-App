package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/5 16:25
 * 就是PostNerResult增加Score
 * @see com.czy.api.domain.ao.post.PostNerResult
 */
@Data
public class NerFeatureScoreDaysAo {
    private String keyWord;
    private String nerType;
    private List<ScoreDaysAo> scoreDaysAoList = new ArrayList<>();

    public boolean isEmpty() {
        return keyWord == null || keyWord.isEmpty() || nerType == null || nerType.isEmpty();
    }

}
