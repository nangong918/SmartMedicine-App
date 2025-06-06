package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.RecipesDo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface RecipesRepository extends Neo4jRepository<RecipesDo, Long> {
    String CQL_USER_RECIPES = UserFeatureRepository.RELS_USER_RECIPES + "|" + PostRepository.RELS_POST_RECIPES + "|do_eat|not_eat";

    @Query("MATCH (n:菜谱) " +
            "WHERE n.name = $name " +
            "RETURN n")
    RecipesDo findByName(@Param("name")String name);
}
