package com.czy.feature.service.impl;

import com.czy.api.constant.feature.PostTypeEnum;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.mapper.PostRepository;
import com.czy.feature.service.PostFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/5/10 14:03
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostFeatureServiceImpl implements PostFeatureService {
    private final PostRepository postRepository;

    // TODO 优化：特征查找之后存储在Redis，缓存击穿再执行数据库查询。
    //  并且更改策略，要预先加载当前热门的Top-k
    @Override
    public PostFeatureAo getPostFeature(Long postId) {
        List<DiseaseDo> diseases = postRepository.findDiseasesByPostId(postId);
        List<ChecksDo> checks = postRepository.findChecksByPostId(postId);
        List<DepartmentsDo> departments = postRepository.findDepartmentsByPostId(postId);
        List<DrugsDo> drugs = postRepository.findDrugsByPostId(postId);
        List<FoodsDo> foods = postRepository.findFoodsByPostId(postId);
        List<ProducersDo> producers = postRepository.findProducersByPostId(postId);
        List<RecipesDo> recipes = postRepository.findRecipesByPostId(postId);
        List<SymptomsDo> symptoms = postRepository.findSymptomsByPostId(postId);
        PostFeatureAo postFeatureAo = new PostFeatureAo();
        List<PostNerResult> postNerResultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(diseases)){
            for (DiseaseDo disease : diseases) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(disease.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.DISEASES.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(checks)){
            for (ChecksDo check : checks) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(check.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.CHECKS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(departments)){
            for (DepartmentsDo department : departments) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(department.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.DEPARTMENTS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(drugs)){
            for (DrugsDo drug : drugs) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(drug.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.DRUGS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(foods)){
            for (FoodsDo food : foods) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(food.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.FOODS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(producers)){
            for (ProducersDo producer : producers) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(producer.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.PRODUCERS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(recipes)){
            for (RecipesDo recipe : recipes) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(recipe.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.RECIPES.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(symptoms)){
            for (SymptomsDo symptom : symptoms) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(symptom.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.SYMPTOMS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        postFeatureAo.setPostNerResultList(postNerResultList);
        Optional<PostNeo4jDo> postNeo4jDo = postRepository.findById(postId);
        postNeo4jDo.ifPresent(post -> {
            // 处理 post 对象
            if (StringUtils.hasText(post.getLabel())){
                PostTypeEnum postTypeEnum = PostTypeEnum.getByName(post.getLabel());
                postFeatureAo.setPostType(postTypeEnum.getCode());
            }
        });
        return postFeatureAo;
    }

}
