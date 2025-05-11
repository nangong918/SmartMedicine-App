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
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.mapper.*;
import com.czy.feature.service.FeatureStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 16:42
 *
 * TODO 待修复
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

    private void saveUserRelation(UserFeatureNeo4jDo user) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
    }


    @Override
    public void createRelationUserWithDiseases(UserFeatureNeo4jDo user, List<DiseaseDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithChecks(UserFeatureNeo4jDo user, List<ChecksDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithDepartments(UserFeatureNeo4jDo user, List<DepartmentsDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithDrugs(UserFeatureNeo4jDo user, List<DrugsDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithFoods(UserFeatureNeo4jDo user, List<FoodsDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithProducers(UserFeatureNeo4jDo user, List<ProducersDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithRecipes(UserFeatureNeo4jDo user, List<RecipesDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithSymptoms(UserFeatureNeo4jDo user, List<SymptomsDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void createRelationUserWithPosts(UserFeatureNeo4jDo user, List<PostNeo4jDo> dos, List<Integer> scoreList) {
        saveUserRelation(user);
    }

    @Override
    public void deleteUserRelation(UserFeatureNeo4jDo user) {
        userFeatureRepository.deletePostWithRelations(user.getName());
    }
}
