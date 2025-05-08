package com.czy.search.rule;

import com.czy.api.api.feature.DiseasesNeo4jService;
import com.czy.api.api.post.PostSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/8 11:08
 */
@Slf4j
@Component
public class Rule4SymptomsFindDiseases {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private DiseasesNeo4jService diseasesNeo4jService;

    public List<Long> execute(List<String> symptomNames){
        List<String> diseaseNames = diseasesNeo4jService.findSymptomsFindDiseases(symptomNames);
        List<Long> allPostIds = new ArrayList<>();
        for (String diseaseName : diseaseNames) {
            // 查询出症状可能是什么疾病导致的，然后将疾病返回
            List<Long> postIds = postSearchService.searchPostIdsByLikeTitle(diseaseName);
            allPostIds.addAll(postIds);
        }
        return allPostIds;
    }

}
