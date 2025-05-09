package com.czy.search.rule;

import com.czy.api.api.feature.DiseasesNeo4jService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.domain.ao.post.PostSearchEsAo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 18:07
 *  疾病如果伴随某些症状，则搜索（疾病 + 症状）
 */
@Slf4j
@Component
public class Rule2AccompanyingSymptoms {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private DiseasesNeo4jService diseasesNeo4jService;

    public List<Long> execute(String diseaseName) {
        List<String> symptomNames = diseasesNeo4jService.findDiseaseWithAccompanyingSymptoms(diseaseName);
        List<Long> postIds = new ArrayList<>();
        for (String symptomName : symptomNames) {
            List<String> keyWords = new ArrayList<>();
            keyWords.add(diseaseName);
            keyWords.add(symptomName);
            List<PostSearchEsAo> postSearchEsAoList = postSearchService.searchByKeywords(keyWords);
            if (postSearchEsAoList.isEmpty()){
                continue;
            }
            for (PostSearchEsAo postSearchEsAo : postSearchEsAoList) {
                postIds.add(postSearchEsAo.getPostDetailEsDo().getId());
            }
        }
        return postIds;
    }

}
