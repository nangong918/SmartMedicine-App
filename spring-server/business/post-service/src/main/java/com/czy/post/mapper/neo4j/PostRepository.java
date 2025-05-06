package com.czy.post.mapper.neo4j;


import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.post.post.PostNeo4jDo;
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

    @Query("MATCH (p:post)-[:post_association]->(d:疾病) " +
            "WHERE id(p) = $postId RETURN d")
    List<DiseaseDo> findDiseasesByPostId(Long postId);
}
