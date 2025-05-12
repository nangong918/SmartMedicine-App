package com.czy.api.mapper;


import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.neo4j.rels.UserPostRelation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    // user_posts
    String RELS_USER_POSTS = "user_post";
    // user_post_label
    String RELS_USER_POST_LABEL = "user_post_label";


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

    // 创建用户和帖子的关系
//    @Query("MATCH (u:user) WHERE u.id = $userId " +
//            "MATCH (p:post) WHERE p.id = $postId" +
//            "MERGE (u)-[r:user_post]->(p) " +
//            "ON CREATE SET r.clickTimes = 1" +
//            "ON MATCH SET r.clickTimes = r.clickTimes + 1" +
//            "RETURN r")
//    UserPostRelation createUserPostRelation(@Param("userId") Long userId,
//                                            @Param("postId") Long postId);
    @Query("MATCH (u:user {id: $userId}) " +
            "MATCH (p:post {id: $postId}) " +
            "MERGE (u)-[r:user_post]->(p) " +
            "ON CREATE SET r.clickTimes = 1, " +
            "r.implicitScore = 0.0, " +
            "r.explicitScore = 0.0, " +
            "r.lastUpdateTimestamp = datetime() " +
            "ON MATCH SET r.clickTimes = r.clickTimes + 1, " +
            "ON MATCH SET r.clickTimes = r.clickTimes + 1, r.lastUpdateTime = datetime() " +
            "RETURN r")
    UserPostRelation createUserPostRelation(@Param("userId") Long userId,
                                            @Param("postId") Long postId);

    @Query("MATCH (u:user) WHERE u.id = $userId " +
            "MATCH (d:`${targetLabel}`) WHERE d.name = $targetName " +
            "MERGE (u)-[r:`${relationType}`]->(d) " +
            "ON CREATE SET r.clickTimes = 1, " +
            "r.implicitScore = 0.0, " +
            "r.explicitScore = 0.0, " +
            "r.lastUpdateTimestamp = datetime() " +
            "ON MATCH SET r.clickTimes = r.clickTimes + 1, " +
            "r.lastUpdateTimestamp = datetime()")
    void createUserEntityPostRelation(@Param("userId") Long userId,
                                      @Param("targetLabel") String targetLabel,
                                      @Param("targetName") String targetName,
                                      @Param("relationType") String relationType);

    @Query("MATCH (u:user)-[r:`${relationType}`]->(d:`${targetLabel}`) " +
            "WHERE u.id = $userId AND d.name = $targetName " +
            "SET r.implicitScore = $implicitScore, " +
            "r.explicitScore = $explicitScore, " +
            "r.lastUpdateTimestamp = datetime()")
    void updateUserEntityPostRelation(@Param("userId") Long userId,
                                      @Param("targetLabel") String targetLabel,
                                      @Param("targetName") String targetName,
                                      @Param("relationType") String relationType,
                                      @Param("implicitScore") Double implicitScore,
                                      @Param("explicitScore") Double explicitScore);

    // 修改查询方法
    @Query("MATCH (u:user)-[r:`${relationType}`]->(d:`${targetLabel}`) " +
            "WHERE u.id = $userId AND d.name = $targetName " +
            "RETURN {implicitScore: r.implicitScore, explicitScore: r.explicitScore}")
    Optional<Map<String, Double>> findUserEntityPostRelation(@Param("userId") Long userId,
                                                             @Param("targetLabel") String targetLabel,
                                                             @Param("targetName") String targetName,
                                                             @Param("relationType") String relationType);

    // 查询用户和帖子的关系
    @Query("MATCH (u:user)-[r:user_post]->(p:post) " +
            "WHERE u.id = $userId AND p.id = $postId " +
            "RETURN r")
    Optional<UserPostRelation> findUserPostRelation(@Param("userId") Long userId,
                                                    @Param("postId") Long postId);

    // 查询用户的所有帖子关系
    @Query("MATCH (u:user)-[r:user_post]->(p:post) " +
            "WHERE u.id = $userId " +
            "RETURN r ORDER BY r.clickTimes DESC")
    List<UserPostRelation> findAllUserPostRelations(@Param("userId") Long userId);

    // 删除特定关系
    @Query("MATCH (u:user)-[r:user_post]->(p:post) " +
            "WHERE u.id = $userId AND p.id = $postId " +
            "DELETE r")
    void deleteUserPostRelation(@Param("userId") Long userId,
                                @Param("postId") Long postId);

    // 设置关系score
    @Query("MATCH (u:user)-[r:user_post]->(p:post) " +
            "WHERE u.id = $userId AND p.id = $postId " +
            "SET r.score = $score")
    void setUserPostRelationClickTimes(@Param("userId") Long userId,
                                  @Param("postId") Long postId,
                                  @Param("score") double score);

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

    // 根据 userId 查找 user 与其他全部节点的关系
    @Query("MATCH (p:user)-[:user_association]->(d:疾病) " +
            "WHERE id(p) = $userId RETURN d")
    List<DiseaseDo> findDiseasesByPostId(Long userId);
}
