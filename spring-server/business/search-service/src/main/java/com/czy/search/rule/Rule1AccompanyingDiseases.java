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
 * @date 2025/5/7 17:25
 * 规则：如果某种疾病存在伴随疾病，则搜索（疾病 + 伴随疾病）
 */
@Slf4j
@Component
public class Rule1AccompanyingDiseases {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private DiseasesNeo4jService diseasesNeo4jService;

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