package com.czy.search.rule;


import com.czy.api.api.feature.DiseasesNeo4jService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.domain.ao.post.PostSearchEsAo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 17:25
 * 规则：疾病如果伴随某些症状，则搜索（疾病 + 症状）
 */
public class Rule1AccompanyingDiseases {

    private final PostSearchService postSearchService;
    private final DiseasesNeo4jService diseasesNeo4jService;

    public Rule1AccompanyingDiseases(
            DiseasesNeo4jService diseasesNeo4jService,
            PostSearchService postSearchService
    ) {
        this.postSearchService = postSearchService;
        this.diseasesNeo4jService = diseasesNeo4jService;
    }

    public List<Long> execute(String diseaseName){
        // 查询出伴随的疾病
        List<String> accompanyDiseases = diseasesNeo4jService.findDiseaseWithAccompanyingDiseases(diseaseName);
        List<Long> postIds = new ArrayList<>();
        if (!accompanyDiseases.isEmpty()){
            for (String accompanyDisease : accompanyDiseases) {
                List<String> keyWords = new ArrayList<>();
                keyWords.add(diseaseName);
                keyWords.add(accompanyDisease);
                List<PostSearchEsAo> postSearchEsAoList = postSearchService.searchByKeywords(keyWords);
                if (postSearchEsAoList.isEmpty()){
                    continue;
                }
                for  (PostSearchEsAo postSearchEsAo : postSearchEsAoList) {
                    postIds.add(postSearchEsAo.getPostDetailEsDo().getId());
                }
            }
        }
        return postIds;
    }

}