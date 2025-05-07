package com.czy.api.api.feature;

import com.czy.api.domain.Do.neo4j.DiseaseDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 17:34
 */
public interface DiseasesNeo4jService {

    // 查询disease是否存在伴随疾病
    List<String> findDiseaseWithAccompanyingDiseases(String diseaseName);
    // 查询disease是否存在伴随症状
    List<String> findDiseaseWithAccompanyingSymptoms(String diseaseName);
}
