package com.czy.api.mapper;


import com.czy.api.domain.Do.neo4j.TestNeo4jDo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/6/3 17:43
 */
@Repository
public interface TestRepository extends Neo4jRepository<TestNeo4jDo, Long> {

    @Query( "MATCH (n:test) " +
            "WHERE n.account = $account RETURN n")
    Optional<TestNeo4jDo> findByAccount(@Param("account") String account);

    @Query( "MATCH (n:test) " +
            "WHERE n.test_id = $testId RETURN n")
    Optional<TestNeo4jDo> findByTestId(@Param("testId") Long testId);
}
