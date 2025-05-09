package com.czy.api.api.feature;


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
    // 查询disease是存在建议：药品，食物，菜谱
    List<String> findDiseaseWithSuggestions(String diseaseName);
    // 查询disease是存在检查Solution
    List<String> findSymptomsFindDiseases(List<String> symptomNames);
}
