package com.czy.api.mapper;


import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
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
public interface UserFeatureRepository extends Neo4jRepository<UserFeatureNeo4jDo, Long> {
    // user_diseases
    String RELS_USER_DISEASES = "user_diseases";
    // user_checks
    String RELS_USER_CHECKS = "user_checks";
    // user_departments
    String RELS_USER_DEPARTMENTS = "user_departments";
    // user_drugs
    String RELS_USER_DRUGS = "user_drugs";
    // user_foods
    String RELS_USER_FOODS = "user_foods";
    // user_producers
    String RELS_USER_PRODUCERS = "user_producers";
    // user_recipes
    String RELS_USER_RECIPES = "user_recipes";
    // user_symptoms
    String RELS_USER_SYMPTOMS = "user_symptoms";
    String RELS_USER_POSTS = "user_post";


    UserFeatureNeo4jDo findByAccount(String account);
    UserFeatureNeo4jDo findByName(String name);



    // 使用 MERGE 来避免重复关系：而不是使用CREATE
    @Query("MATCH (p:user) WHERE p.name = $userName " +
            "MATCH (d:`${targetLabel}`) WHERE d.name = $targetName " +  // 使用反引号和占位符
            "MERGE (p)-[:`${relationType}`]->(d)")                    // 动态关系类型
    void createDynamicRelationship(
            @Param("userName") String userName,
            @Param("targetLabel") String targetLabel,
            @Param("targetName") String targetName,
            @Param("relationType") String relationType
    );

    // 创建关系并设置权重
    @Query("MATCH (u:user) WHERE u.name = $userName " +
            "MATCH (t:`${targetLabel}`) WHERE t.name = $targetName " +
            "MERGE (u)-[r:`${relationType}`]->(t) " +
            "ON CREATE SET r.weight = $initialWeight " +
            "ON MATCH SET r.weight = r.weight + $increment")
    void upsertWeightedRelationship(
            @Param("userName") String userName,
            @Param("targetLabel") String targetLabel,
            @Param("targetName") String targetName,
            @Param("relationType") String relationType,
            @Param("initialWeight") double initialWeight,
            @Param("increment") double increment
    );

    // 删除某条关系
    @Query("MATCH (p:user)-[r:`${relationType}`]->(d:`${targetLabel}`) " +
            "WHERE p.name = $userName AND d.name = $targetName " +
            "DELETE r")
    void deleteDynamicRelationship(
            @Param("userName") String userName,
            @Param("targetLabel") String targetLabel,
            @Param("targetName") String targetName,
            @Param("relationType") String relationType
    );

    // 删除user与其他全部节点的关系
    @Query("MATCH (p:user {name: $userName}) " +
            "MATCH (p)-[r]->() " +
            "DELETE r " +  // 删除与其他节点的关系
            "DELETE p")    // 删除该 user 节点
    void deletePostWithRelations(@Param("userName") String userName);

    // 根据 ID 删除 user 与其他全部节点的关系
    @Query("MATCH (p:user) WHERE id(p) = $userId " +
            "MATCH (p)-[r]->() " +
            "DELETE r " +  // 删除与其他节点的关系
            "DELETE p")    // 删除该 user 节点
    void deletePostByIdWithRelations(@Param("userId") Long userId);

    // 修改某条关系
    @Query("MATCH (d:`${targetLabel}`) WHERE d.name = $targetName " +
            "SET d.propertyName = $newValue")
    void updateNodeProperty(
            @Param("targetLabel") String targetLabel,
            @Param("targetName") String targetName,
            @Param("newValue") String newValue
    );

    @Query("MATCH (p:user)-[:user_association]->(d:疾病) " +
            "WHERE id(p) = $userId RETURN d")
    List<DiseaseDo> findDiseasesByPostId(Long userId);
}
