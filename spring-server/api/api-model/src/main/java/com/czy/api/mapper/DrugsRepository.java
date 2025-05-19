package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.DrugsDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface DrugsRepository extends Neo4jRepository<DrugsDo, Long> {
    String CQL_USER_DRUGS = UserFeatureRepository.RELS_USER_DRUGS + "|" + PostRepository.RELS_POST_DRUGS + "|has_common_drug|recommand_drug";

    DrugsDo findByName(String name);
}
