package com.czy.feature.mapper;


import com.czy.api.domain.Do.post.post.PostNeo4jDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;


/**
 * @author 13225
 * @date 2025/5/6 16:03
 */
@Repository
public interface PostRepository extends Neo4jRepository<PostNeo4jDo, Long> {

    PostNeo4jDo findByTitle(String title);
    PostNeo4jDo findByName(String name);

}
