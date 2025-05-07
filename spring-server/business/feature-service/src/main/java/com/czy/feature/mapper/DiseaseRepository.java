package com.czy.feature.mapper;

/**
 * @author 13225
 * @date 2025/4/25 17:01
 */


import com.czy.api.domain.Do.neo4j.DiseaseDo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DiseaseRepository extends Neo4jRepository<DiseaseDo, Long> {
    DiseaseDo findByName(String name);

//    @Query("MATCH (d:疾病 {name: $name})-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(related) RETURN related LIMIT 10")
//    List<DiseaseDo> findRelatedEntities(@Param("name") String name);

    @Query("MATCH (d:疾病 {name: $name})-[:has_symptom]->(s) RETURN s LIMIT 20")
    List<Map<String, String>> findRelatedSymptom(@Param("name") String name);

    @Query("MATCH (d:疾病 {name: $name})-[:has_symptom]->(s) RETURN s.name LIMIT 20")
    List<String> findRelatedSymptomName(@Param("name") String name);

    @Query("MATCH (d:疾病 {name: $name})-[r:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(related) " +
            "RETURN related, labels(related) as labels, type(r) as relationType " +
            "LIMIT 20")
    List<Map<String, Object>> findRelatedEntities(@Param("name") String name);

    // 写在这里：查询一个实体的关系list
    @Query("MATCH (e:疾病 {name: $name})-[:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(d) " +
            "RETURN id(d) AS diseaseId")
    List<Long> findDiseaseIdsForEntity(@Param("name") String name);

    @Query("MATCH (e:疾病 {name: $name})-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(related) " +
            "RETURN id(related) AS neighborId")
    List<Long> findNeighborIdsForEntity(@Param("name") String name);

    @Query("MATCH (e1:疾病 {name: $name1}), (e2:疾病 {name: $name2}), " +
            "p = shortestPath((e1)-[*]-(e2)) " +
            "RETURN length(p) AS pathLength")
    Integer findShortestPathLength(@Param("name1") String name1, @Param("name2") String name2);


    /**
     * MATCH (d1:疾病 {name: "大叶性肺炎"})-[:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(related1),
     * (d2:疾病)-[:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(related2)
     * WHERE d2.name <> "大叶性肺炎"
     * WITH d2, collect(id(related1)) AS ids1, collect(id(related2)) AS ids2
     * WITH d2, ids1, ids2,
     * [id IN ids1 WHERE id IN ids2] AS intersection
     * RETURN d2.name AS diseaseName,
     * CASE size(ids1) + size(ids2)
     * WHEN 0 THEN 0.0
     * ELSE size(intersection) * 1.0 / (size(ids1) + size(ids2))
     * END AS jaccardIndex
     * ORDER BY jaccardIndex DESC
     * LIMIT 10
     * @param name
     * @return
     */
    @Query("MATCH (d1:疾病 {name: $name})-[:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(related1), " +
            "(d2:疾病)-[:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(related2) " +
            "WHERE d2.name <> $name " +
            "WITH d2, collect(id(related1)) AS ids1, collect(id(related2)) AS ids2 " +
            "WITH d2, ids1, ids2, " +
            "  [id IN ids1 WHERE id IN ids2] AS intersection " +
            "RETURN d2.name AS diseaseName, " +
            "       CASE size(ids1) + size(ids2) " +
            "           WHEN 0 THEN 0.0 " +
            "           ELSE size(intersection) * 1.0 / (size(ids1) + size(ids2)) " +
            "       END AS jaccardIndex " +
            "ORDER BY jaccardIndex DESC " +
            "LIMIT 10")
    List<Map<String, Object>> findTopSimilarDiseasesByJaccard(@Param("name") String name);


    /**
     * MATCH (d1:疾病 {name: $diseaseName})-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(neighbor1)
     * WITH d1, collect(id(neighbor1)) AS neighbors1
     *
     * MATCH (d2:疾病)-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(neighbor2)
     * WHERE d2 <> d1 // 确保不与自身比较
     * WITH d1, d2, neighbors1, collect(id(neighbor2)) AS neighbors2
     *
     * // 计算共同邻居
     * WITH d1, d2, neighbors1, neighbors2,
     *      [id IN neighbors1 WHERE id IN neighbors2] AS commonNeighbors
     *
     * // 计算并集的大小
     * WITH d1, d2, commonNeighbors,
     *      size(neighbors1) + size(neighbors2) - size(commonNeighbors) AS allNeighborsCount
     *
     * RETURN d2.name AS diseaseName,
     *        size(commonNeighbors) AS commonNeighborsCount,
     *        allNeighborsCount,
     *        CASE allNeighborsCount
     *            WHEN 0 THEN 0.0
     *            ELSE size(commonNeighbors) * 1.0 / allNeighborsCount
     *        END AS similarityScore
     * ORDER BY similarityScore DESC
     * LIMIT 10
     * @param diseaseName
     * @return
     */
    @Query("MATCH (d1:疾病 {name: $diseaseName})-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(neighbor1) " +
            "WITH d1, collect(id(neighbor1)) AS neighbors1 " +
            "MATCH (d2:疾病)-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(neighbor2) " +
            "WHERE d2 <> d1 " +
            "WITH d1, d2, neighbors1, collect(id(neighbor2)) AS neighbors2 " +
            "WITH d1, d2, neighbors1, neighbors2, " +
            "     [id IN neighbors1 WHERE id IN neighbors2] AS commonNeighbors " +
            "WITH d1, d2, commonNeighbors, " +
            "     size(neighbors1) + size(neighbors2) - size(commonNeighbors) AS allNeighborsCount " +
            "RETURN d2.name AS diseaseName, " +
            "       size(commonNeighbors) AS commonNeighborsCount, " +
            "       allNeighborsCount, " +
            "       CASE allNeighborsCount " +
            "           WHEN 0 THEN 0.0 " +
            "           ELSE size(commonNeighbors) * 1.0 / allNeighborsCount " +
            "       END AS similarityScore " +
            "ORDER BY similarityScore DESC " +
            "LIMIT 10")
    List<Map<String, Object>> findTopSimilarDiseasesByNeighbor(@Param("diseaseName") String diseaseName);


    /**
     * MATCH (d1:疾病 {name: '大叶性肺炎'})-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(d2:疾病)
     * WHERE d1 <> d2 // 确保不与自身比较
     * RETURN d2.name AS diseaseName
     * LIMIT 10
     * @param diseaseName
     * @return
     */
    @Query("MATCH (d1:疾病 {name: $diseaseName})-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(d2:疾病) " +
            "WHERE d1 <> d2 " +
            "RETURN d2.name AS diseaseName " +
            "LIMIT 10")
    List<Map<String, Object>> findTopSimilarDiseasesByPath1(@Param("diseaseName") String diseaseName);


    /**
     * 查询是否存在伴随疾病
     * @param diseaseName   疾病名称
     * @return              伴随疾病名称列表 双向的关系
     */
    @Query("MATCH (d1:疾病 {name: $diseaseName})-[r:acompany_with]-(d2:疾病) " +
            "RETURN d2.name AS diseaseName")
    List<Map<String, Object>> findAccompanyingDiseases(@Param("diseaseName") String diseaseName);

}
