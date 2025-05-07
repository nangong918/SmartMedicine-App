package com.czy.feature.service.impl;

import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.post.post.PostNeo4jDo;
import com.czy.feature.mapper.*;
import com.czy.feature.service.FeatureStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 16:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeatureStorageServiceImpl implements FeatureStorageService {

    // post的更新是删除全部更新特征，但是user并不是，user是在原有的基础上添加
    private final UserFeatureRepository userFeatureRepository;
    private final ChecksRepository checksRepository;
    private final DepartmentsRepository departmentsRepository;
    private final DiseaseRepository diseaseRepository;
    private final DrugsRepository drugsRepository;
    private final FoodsRepository foodsRepository;
    private final PostRepository postRepository;
    private final ProducersRepository producersRepository;
    private final RecipesRepository recipesRepository;
    private final SymptomsRepository symptomsRepository;
    @Override
    public void createRelationUserWithDiseases(UserFeatureNeo4jDo user, List<DiseaseDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (DiseaseDo disease : dos) {
            DiseaseDo findDiseaseDo = diseaseRepository.findByName(disease.getName());
            if (findDiseaseDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "疾病",
                        disease.getName(), UserFeatureRepository.RELS_USER_DISEASES,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithChecks(UserFeatureNeo4jDo user, List<ChecksDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (ChecksDo checks : dos) {
            ChecksDo findChecksDo = checksRepository.findByName(checks.getName());
            if (findChecksDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "检查",
                        checks.getName(), UserFeatureRepository.RELS_USER_CHECKS,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithDepartments(UserFeatureNeo4jDo user, List<DepartmentsDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (DepartmentsDo departments : dos) {
            DepartmentsDo findDepartmentsDo = departmentsRepository.findByName(departments.getName());
            if (findDepartmentsDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "科室",
                        departments.getName(), UserFeatureRepository.RELS_USER_DEPARTMENTS,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithDrugs(UserFeatureNeo4jDo user, List<DrugsDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (DrugsDo drugs : dos) {
            DrugsDo findDrugsDo = drugsRepository.findByName(drugs.getName());
            if (findDrugsDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "药品",
                        drugs.getName(), UserFeatureRepository.RELS_USER_DRUGS,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithFoods(UserFeatureNeo4jDo user, List<FoodsDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (FoodsDo foods : dos) {
            FoodsDo findFoodsDo = foodsRepository.findByName(foods.getName());
            if (findFoodsDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "食物",
                        foods.getName(), UserFeatureRepository.RELS_USER_FOODS,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithProducers(UserFeatureNeo4jDo user, List<ProducersDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (ProducersDo producers : dos) {
            ProducersDo findProducersDo = producersRepository.findByName(producers.getName());
            if (findProducersDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "药企",
                        producers.getName(), UserFeatureRepository.RELS_USER_PRODUCERS,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithRecipes(UserFeatureNeo4jDo user, List<RecipesDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (RecipesDo recipes : dos) {
            RecipesDo findRecipesDo = recipesRepository.findByName(recipes.getName());
            if (findRecipesDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "菜谱",
                        recipes.getName(), UserFeatureRepository.RELS_USER_RECIPES,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithSymptoms(UserFeatureNeo4jDo user, List<SymptomsDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (SymptomsDo symptoms : dos) {
            SymptomsDo findSymptomsDo = symptomsRepository.findByName(symptoms.getName());
            if (findSymptomsDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "症状",
                        symptoms.getName(), UserFeatureRepository.RELS_USER_SYMPTOMS,
                        1.0,
                        1.0);
            }
        }
    }

    @Override
    public void createRelationUserWithPosts(UserFeatureNeo4jDo user, List<PostNeo4jDo> dos) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
        for (PostNeo4jDo post : dos) {
            PostNeo4jDo findPostDo = postRepository.findByName(post.getName());
            if (findPostDo != null){
                userFeatureRepository.upsertWeightedRelationship(
                        user.getName(), "帖子",
                        post.getName(), UserFeatureRepository.RELS_USER_POSTS,
                        1.0,
                        1.0);
            }
        }
    }


    @Override
    public void deleteUserRelation(UserFeatureNeo4jDo user) {
        userFeatureRepository.deletePostWithRelations(user.getName());
    }
}
