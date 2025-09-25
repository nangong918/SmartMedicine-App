package com.ai.medicine.mapper;

import com.ai.medicine.domain.Do.neo4j.DiseaseDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *@author 13225
 *@date 2025/9/22 17:33
 */
@Repository
public interface DiseaseRepository extends Neo4jRepository<DiseaseDo, Long> {

    @Query("MATCH (n:疾病) " +
            "WHERE n.name = $name " +
            "RETURN n")
    DiseaseDo findByName(@Param("name") String name);

}
