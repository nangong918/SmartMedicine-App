package com.czy.post.service;

import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.ao.post.PostAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 21:10
 */
public interface PostTransactionService {
    // es + mongo的事务
    void storePostToDatabase(PostAo postAo);

    void deletePostContentById(Long id);

    void updatePostContentToDatabase(PostAo postAo);

    // relation：diseases
    void createRelationPostWithDiseases(PostNeo4jDo post, List<DiseaseDo> dos);
    // relation：checks
    void createRelationPostWithChecks(PostNeo4jDo post, List<ChecksDo> dos);
    // relation：departments
    void createRelationPostWithDepartments(PostNeo4jDo post, List<DepartmentsDo> dos);
    // relation：drugs
    void createRelationPostWithDrugs(PostNeo4jDo post, List<DrugsDo> dos);
    // relation：foods
    void createRelationPostWithFoods(PostNeo4jDo post, List<FoodsDo> dos);
    // relation：producers
    void createRelationPostWithProducers(PostNeo4jDo post, List<ProducersDo> dos);
    // relation：recipes
    void createRelationPostWithRecipes(PostNeo4jDo post, List<RecipesDo> dos);
    // relation：symptoms
    void createRelationPostWithSymptoms(PostNeo4jDo post, List<SymptomsDo> dos);
}
