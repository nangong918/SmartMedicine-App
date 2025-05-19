package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.FoodsDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface FoodsRepository extends Neo4jRepository<FoodsDo, Long> {
    String CQL_USER_FOODS = UserFeatureRepository.RELS_USER_FOODS + "|" + PostRepository.RELS_POST_FOODS + "|do_eat|not_eat";

    FoodsDo findByName(String name);
}
