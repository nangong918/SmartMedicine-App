package com.czy.api.mapper;


import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/6 16:03
 */
@Repository
public interface PostRepository extends Neo4jRepository<PostNeo4jDo, Long> {
    // post_diseases
    String RELS_POST_DISEASES = "post_diseases";
    // post_checks
    String RELS_POST_CHECKS = "post_checks";
    // post_departments
    String RELS_POST_DEPARTMENTS = "post_departments";
    // post_drugs
    String RELS_POST_DRUGS = "post_drugs";
    // post_foods
    String RELS_POST_FOODS = "post_foods";
    // post_producers
    String RELS_POST_PRODUCERS = "post_producers";
    // post_recipes
    String RELS_POST_RECIPES = "post_recipes";
    // post_symptoms
    String RELS_POST_SYMPTOMS = "post_symptoms";


    PostNeo4jDo findByTitle(String title);
    PostNeo4jDo findByName(String name);


    // 使用 MERGE 来避免重复关系：而不是使用CREATE
    @Query("MATCH (p:post) WHERE p.name = $postName " +
            "MATCH (d:`${targetLabel}`) WHERE d.name = $targetName " +  // 使用反引号和占位符
            "MERGE (p)-[:`${relationType}`]->(d)")                    // 动态关系类型
    void createDynamicRelationship(
            @Param("postName") String postName,
            @Param("targetLabel") String targetLabel,
            @Param("targetName") String targetName,
            @Param("relationType") String relationType
    );

    // 删除某条关系
    @Query("MATCH (p:post)-[r:`${relationType}`]->(d:`${targetLabel}`) " +
            "WHERE p.name = $postName AND d.name = $targetName " +
            "DELETE r")
    void deleteDynamicRelationship(
            @Param("postName") String postName,
            @Param("targetLabel") String targetLabel,
            @Param("targetName") String targetName,
            @Param("relationType") String relationType
    );

    // 删除post与其他全部节点的关系
    @Query("MATCH (p:post {name: $postName}) " +
            "MATCH (p)-[r]->() " +
            "DELETE r " +  // 删除与其他节点的关系
            "DELETE p")    // 删除该 post 节点
    void deletePostWithRelations(@Param("postName") String postName);

    // 根据 ID 删除 post 与其他全部节点的关系
    @Query("MATCH (p:post) WHERE id(p) = $postId " +
            "MATCH (p)-[r]->() " +
            "DELETE r " +  // 删除与其他节点的关系
            "DELETE p")    // 删除该 post 节点
    void deletePostByIdWithRelations(@Param("postId") Long postId);

    // 修改某条关系
    @Query("MATCH (d:`${targetLabel}`) WHERE d.name = $targetName " +
            "SET d.propertyName = $newValue")
    void updateNodeProperty(
            @Param("targetLabel") String targetLabel,
            @Param("targetName") String targetName,
            @Param("newValue") String newValue
    );

    // DiseasesDo
    @Query("MATCH (p:post)-[:post_association]->(d:疾病) " +
            "WHERE id(p) = $postId RETURN d")
    List<DiseaseDo> findDiseasesByPostId(Long postId);

    // CheckDo
    @Query("MATCH (p:post)-[:post_checks]->(d:检查) " +
            "WHERE id(p) = $postId RETURN d")
    List<ChecksDo> findChecksByPostId(Long postId);

    // DepartmentsDo
    @Query("MATCH (p:post)-[:post_departments]->(d:科室) " +
            "WHERE id(p) = $postId RETURN d")
    List<DepartmentsDo> findDepartmentsByPostId(Long postId);

    // DrugsDo
    @Query("MATCH (p:post)-[:post_drugs]->(d:药品) " +
            "WHERE id(p) = $postId RETURN d")
    List<DrugsDo> findDrugsByPostId(Long postId);

    // FoodsDo
    @Query("MATCH (p:post)-[:post_foods]->(d:食物) " +
            "WHERE id(p) = $postId RETURN d")
    List<FoodsDo> findFoodsByPostId(Long postId);

    // ProducersDo
    @Query("MATCH (p:post)-[:post_producers]->(d:药企) " +
            "WHERE id(p) = $postId RETURN d")
    List<ProducersDo> findProducersByPostId(Long postId);

    // RecipesDo
    @Query("MATCH (p:post)-[:post_recipes]->(d:菜谱) " +
            "WHERE id(p) = $postId RETURN d")
    List<RecipesDo> findRecipesByPostId(Long postId);

    // SymptomsDo
    @Query("MATCH (p:post)-[:post_symptoms]->(d:症状) " +
            "WHERE id(p) = $postId RETURN d")
    List<SymptomsDo> findSymptomsByPostId(Long postId);
}
