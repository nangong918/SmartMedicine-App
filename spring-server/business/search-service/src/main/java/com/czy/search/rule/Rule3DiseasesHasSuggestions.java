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
 * @date 2025/5/7 18:12
 * 如果疾病存在解决方案：药品，食物，菜谱
 */
@Slf4j
@Component
public class Rule3DiseasesHasSuggestions {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private DiseasesNeo4jService diseasesNeo4jService;


    public List<Long> execute(String diseaseName){
        List<String> suggestions = diseasesNeo4jService.findDiseaseWithSuggestions(diseaseName);
        List<Long> postIds = new ArrayList<>();
        for (String suggestion : suggestions) {
            List<String> keyWords = new ArrayList<>();
            keyWords.add(diseaseName);
            keyWords.add(suggestion);
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
