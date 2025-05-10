package com.czy.feature.service;

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

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 16:34
 */
public interface FeatureStorageService {

    //  createRelationUserWithDiseases
    void createRelationUserWithDiseases(UserFeatureNeo4jDo user, List<DiseaseDo> dos);
    //  createRelationUserWithChecks
    void createRelationUserWithChecks(UserFeatureNeo4jDo user, List<ChecksDo> dos);
    //  createRelationUserWithDepartments
    void createRelationUserWithDepartments(UserFeatureNeo4jDo user, List<DepartmentsDo> dos);
    //  createRelationUserWithDrugs
    void createRelationUserWithDrugs(UserFeatureNeo4jDo user, List<DrugsDo> dos);
    //  createRelationUserWithFoods
    void createRelationUserWithFoods(UserFeatureNeo4jDo user, List<FoodsDo> dos);
    //  createRelationUserWithProducers
    void createRelationUserWithProducers(UserFeatureNeo4jDo user, List<ProducersDo> dos);
    //  createRelationUserWithRecipes
    void createRelationUserWithRecipes(UserFeatureNeo4jDo user, List<RecipesDo> dos);
    //  createRelationUserWithSymptoms
    void createRelationUserWithSymptoms(UserFeatureNeo4jDo user, List<SymptomsDo> dos);
    // createRelationUserWithPosts
    void createRelationUserWithPosts(UserFeatureNeo4jDo user, List<PostNeo4jDo> dos);
    // deleteUserRelation
    void deleteUserRelation(UserFeatureNeo4jDo user);
}
