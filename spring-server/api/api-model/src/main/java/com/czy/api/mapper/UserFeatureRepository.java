package com.czy.api.mapper;


import com.czy.api.domain.Do.neo4j.*;
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
    // user_publish_post
    String RELS_USER_PUBLISH_POST = "user_publish_post";

    @Query( "MATCH (n:user) " +
            "WHERE n.account = $account RETURN n")
    Optional<UserFeatureNeo4jDo> findByAccount(@Param("account") String account);
    @Query( "MATCH (n:user) " +
            "WHERE n.name = $name RETURN n")
    Optional<UserFeatureNeo4jDo> findByName(@Param("name") String name);
    @Query("MATCH (n:user) " +
            "WHERE n.user_id = $userId " +
            "RETURN n")
    Optional<UserFeatureNeo4jDo> findByUserId(@Param("userId") Long userId);



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

    // update user-post RELS
    @Query("MATCH (u:user)-[r:user_post]->(p:post) " +
            "WHERE u.id = $userId AND p.id = $postId " +
            "SET r.clickTimes = $clickTimes, " +
            "r.implicitScore = $implicitScore, " +
            "r.explicitScore = $explicitScore, " +
            "r.lastUpdateTimestamp = datetime()")
    void updateUserPostRelation(@Param("userId") Long userId,
                               @Param("postId") Long postId,
                               @Param("clickTimes") Integer clickTimes,
                               @Param("implicitScore") Double implicitScore,
                               @Param("explicitScore") Double explicitScore);

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

    /**
     * 帮我写代码：
     * 改成如果不存在关系就创建关系，然后这些数据插入进去，
     * 如果存在关系，不是更新哦，注意不是更新，数据叠加进去，并且我要求implicitScore，explicitScore最小值是-10.0，最大值是10.0，这是避免用户某项特征过于突出。
     * @param userId
     * @param targetLabel
     * @param targetName
     * @param relationType
     * @param clickTimes
     * @param implicitScore
     * @param explicitScore
     */
    @Query(
            // 避免MATCH (u:user {id: $userId}), (d:`${targetLabel}` {name: $targetName}) 这样可能会导致不必要的笛卡尔积计算 **
            "MATCH (u:user {id: $userId}) " +
            "MATCH (d:`${targetLabel}` {name: $targetName}) " +
            "MERGE (u)-[r:`${relationType}`]->(d) " +
            "ON CREATE SET " +
            "  r.clickTimes = $clickTimes, " +
            "  r.implicitScore = CASE WHEN $implicitScore > 10.0 THEN 10.0 " +
            "                         WHEN $implicitScore < -10.0 THEN -10.0 " +
            "                         ELSE $implicitScore END, " +
            "  r.explicitScore = CASE WHEN $explicitScore > 10.0 THEN 10.0 " +
            "                         WHEN $explicitScore < -10.0 THEN -10.0 " +
            "                         ELSE $explicitScore END, " +
            "  r.lastUpdateTimestamp = datetime() " +
            "ON MATCH SET " +
            "  r.clickTimes = r.clickTimes + $clickTimes, " +
            "  r.implicitScore = CASE WHEN r.implicitScore + $implicitScore > 10.0 THEN 10.0 " +
            "                         WHEN r.implicitScore + $implicitScore < -10.0 THEN -10.0 " +
            "                         ELSE r.implicitScore + $implicitScore END, " +
            "  r.explicitScore = CASE WHEN r.explicitScore + $explicitScore > 10.0 THEN 10.0 " +
            "                         WHEN r.explicitScore + $explicitScore < -10.0 THEN -10.0 " +
            "                         ELSE r.explicitScore + $explicitScore END, " +
            "  r.lastUpdateTimestamp = datetime()")
    void saveOrUpdateUserEntityRelation(@Param("userId") Long userId,
                                        @Param("targetLabel") String targetLabel,
                                        @Param("targetName") String targetName,
                                        @Param("relationType") String relationType,
                                        @Param("clickTimes") Integer clickTimes,
                                        @Param("implicitScore") Double implicitScore,
                                        @Param("explicitScore") Double explicitScore);

    /**
     * @param userId
     * @param postId
     * @param clickTimes
     * @param implicitScore
     * @param explicitScore
     */
    @Query(
            "MATCH (u:user {id: $userId}) " +
                    "MATCH (d:post {id: $postId}) " +
                    "MERGE (u)-[r:user_post]->(d) " +
                    "ON CREATE SET " +
                    "  r.clickTimes = $clickTimes, " +
                    "  r.implicitScore = CASE WHEN $implicitScore > 10.0 THEN 10.0 " +
                    "                         WHEN $implicitScore < -10.0 THEN -10.0 " +
                    "                         ELSE $implicitScore END, " +
                    "  r.explicitScore = CASE WHEN $explicitScore > 10.0 THEN 10.0 " +
                    "                         WHEN $explicitScore < -10.0 THEN -10.0 " +
                    "                         ELSE $explicitScore END, " +
                    "  r.lastUpdateTimestamp = datetime() " +
                    "ON MATCH SET " +
                    "  r.clickTimes = r.clickTimes + $clickTimes, " +
                    "  r.implicitScore = CASE WHEN r.implicitScore + $implicitScore > 10.0 THEN 10.0 " +
                    "                         WHEN r.implicitScore + $implicitScore < -10.0 THEN -10.0 " +
                    "                         ELSE r.implicitScore + $implicitScore END, " +
                    "  r.explicitScore = CASE WHEN r.explicitScore + $explicitScore > 10.0 THEN 10.0 " +
                    "                         WHEN r.explicitScore + $explicitScore < -10.0 THEN -10.0 " +
                    "                         ELSE r.explicitScore + $explicitScore END, " +
                    "  r.lastUpdateTimestamp = datetime()")
    void saveOrUpdateUserPostRelation(@Param("userId") Long userId,
                                        @Param("postId") Long postId,
                                        @Param("clickTimes") Integer clickTimes,
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

    @Query("MATCH (u:user)-[r:`${relationType}`]->(d:`${targetLabel}`) " +
            "WHERE u.id = $userId AND d.name = $targetName " +
            "RETURN {clickTimes: r.clickTimes, implicitScore: r.implicitScore, explicitScore: r.explicitScore}")
    Optional<Map<String, Object>> findUserEntityPostRelationScoreAo(@Param("userId") Long userId,
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
    // 疾病
    @Query("MATCH (p:user)-[:user_association]->(d:疾病) " +
            "WHERE id(p) = $userId RETURN d")
    List<DiseaseDo> findDiseasesByPostId(Long userId);

    // 检查
    @Query("MATCH (p:user)-[:user_checks]->(c:检查) " +
            "WHERE id(p) = $userId RETURN c")
    List<ChecksDo> findChecksByPostId(Long userId);

    // 科室
    @Query("MATCH (p:user)-[:user_department]->(d:科室) " +
            "WHERE id(p) = $userId RETURN d")
    List<DepartmentsDo> findDepartmentsByPostId(Long userId);

    // 药品
    @Query("MATCH (p:user)-[:user_drug]->(d:药品) " +
            "WHERE id(p) = $userId RETURN d")
    List<DrugsDo> findDrugsByPostId(Long userId);

    // 食物
    @Query("MATCH (p:user)-[:user_food]->(f:食物) " +
            "WHERE id(p) = $userId RETURN f")
    List<FoodsDo> findFoodsByPostId(Long userId);

    // producers
    @Query("MATCH (p:user)-[:user_producers]->(s:药企) " +
            "WHERE id(p) = $userId RETURN s")
    List<ProducersDo> findProducersByPostId(Long userId);

    // recipes
    @Query("MATCH (p:user)-[:user_recipes]->(r:菜谱) " +
            "WHERE id(p) = $userId RETURN r")
    List<RecipesDo> findRecipesByPostId(Long userId);

    // 症状
    @Query("MATCH (p:user)-[:user_symptom]->(s:症状) " +
            "WHERE id(p) = $userId RETURN s")
    List<SymptomsDo> findSymptomsByPostId(Long userId);

    // post
    @Query("MATCH (p:user)-[:user_post]->(p:post) " +
            "WHERE id(p) = $userId RETURN p")
    List<PostNeo4jDo> findPostsByPostId(Long userId);

    // post_label
    @Query("MATCH (p:user)-[:user_post_label]->(l:post_label) " +
            "WHERE id(p) = $userId RETURN l")
    List<PostLabelNeo4jDo> findPostLabelsByPostId(Long userId);

    /// user 画像构建 -> （带有权重的entity集合）
    @Query("MATCH (u:user {id:`${userId}`)-[r:`${relationType}`]->(e:`${entityType}`) " +
            "RETURN " +
            "e.name AS name, " +
            "r.clickTimes AS clickTimes, " +
            "r.implicitScore AS implicitScore, " +
            "r.explicitScore AS explicitScore")
    List<Map<String, Object>> findUserRelatedEntitiesWithWeights(
            @Param("userId") Long userId,
            @Param("entityType") String entityType,
            @Param("relationship") String relationship);

    /// entity - relation - entity

    /**
     * jaccard 相似度
     * @param name          entity name
     * @param entityLabel   entity label
     * @param relationships relationship
     * @param n             limit 个数
     * @return              通过Jaccard相似度计算出的List<Map<String, Object>> 返回内容包含
     * similarEntityName
     * jaccardIndex
     */
    @Query("MATCH (d1:`${entityLabel}` {name: $name})-[:`${relationships}`]->(related1), " +
            "(d2:`${entityLabel}`)-[:`${relationships}`]->(related2) " +
            "WHERE d2.name <> $name " +
            "WITH d2, collect(id(related1)) AS ids1, collect(id(related2)) AS ids2 " +
            "WITH d2, ids1, ids2, " +
            "  [id IN ids1 WHERE id IN ids2] AS intersection " +
            "RETURN d2.name AS similarEntityName, " +
            "       CASE size(ids1) + size(ids2) " +
            "           WHEN 0 THEN 0.0 " +
            "           ELSE size(intersection) * 1.0 / (size(ids1) + size(ids2)) " +
            "       END AS jaccardIndex " +
            "ORDER BY jaccardIndex DESC " +
            "LIMIT $n")
    List<Map<String, Object>> findTopSimilarByJaccard(
            @Param("name") String name,
            @Param("entityLabel") String entityLabel,
            @Param("relationships") String relationships,
            @Param("n") int n);


    /**
     * neighbor 相似度
     * @param name          entity name
     * @param entityLabel   entity label
     * @param relationships relationship
     * @param n             limit 个数
     * @return              通过neighbor相似度计算出的List<Map<String, Object>> 返回内容包含
     * similarEntityName
     * commonNeighborsCount
     * similarityScore
     */
    @Query("MATCH (d1:`${entityLabel}` {name: $name})-[:`${relationships}`]-(neighbor1) " +
            "WITH d1, collect(id(neighbor1)) AS neighbors1 " +
            "MATCH (d2:`${entityLabel}`)-[:`${relationships}`]-(neighbor2) " +
            "WHERE d2 <> d1 " +
            "WITH d1, d2, neighbors1, collect(id(neighbor2)) AS neighbors2 " +
            "WITH d1, d2, neighbors1, neighbors2, " +
            "     [id IN neighbors1 WHERE id IN neighbors2] AS commonNeighbors " +
            "WITH d1, d2, commonNeighbors, " +
            "     size(neighbors1) + size(neighbors2) - size(commonNeighbors) AS allNeighborsCount " +
            "RETURN d2.name AS similarEntityName, " +
            "       size(commonNeighbors) AS commonNeighborsCount, " +
            "       allNeighborsCount, " +
            "       CASE allNeighborsCount " +
            "           WHEN 0 THEN 0.0 " +
            "           ELSE size(commonNeighbors) * 1.0 / allNeighborsCount " +
            "       END AS similarityScore " +
            "ORDER BY similarityScore DESC " +
            "LIMIT $n")
    List<Map<String, Object>> findTopSimilarByNeighbor(
            @Param("name") String name,
            @Param("entityLabel") String entityLabel,
            @Param("relationships") String relationships,
            @Param("n") int n);
}
