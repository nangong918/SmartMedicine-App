package com.czy.feature.service.impl;

import com.czy.api.api.feature.DiseasesNeo4jService;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.feature.mapper.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/7 17:34
 */
@Slf4j
@RequiredArgsConstructor
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class DiseasesNeo4jServiceImpl implements DiseasesNeo4jService {

    private final DiseaseRepository diseaseRepository;

    @Override
    public List<String> findDiseaseWithAccompanyingDiseases(String diseaseName) {
        List<Map<String, Object>> accompanyingDiseases = diseaseRepository.findAccompanyingDiseases(diseaseName);
        List<String> accompanyingDiseaseNames = new ArrayList<>();
        for (Map<String, Object> accompanyingDisease : accompanyingDiseases) {
            String accompanyingDiseaseName = (String) accompanyingDisease.get("diseaseName");
            if (StringUtils.hasText(accompanyingDiseaseName)) {
                accompanyingDiseaseNames.add(accompanyingDiseaseName);
            }
        }
        return accompanyingDiseaseNames;
    }

    @Override
    public List<String> findDiseaseWithAccompanyingSymptoms(String diseaseName) {
        List<Map<String, Object>> accompanyingSymptoms = diseaseRepository.findAccompanyingSymptoms(diseaseName);
        List<String> accompanyingSymptomNames = new ArrayList<>();
        for (Map<String, Object> accompanyingSymptom : accompanyingSymptoms) {
            String accompanyingSymptomName = (String) accompanyingSymptom.get("symptomName");
            if (StringUtils.hasText(accompanyingSymptomName)) {
                accompanyingSymptomNames.add(accompanyingSymptomName);
            }
        }
        return accompanyingSymptomNames;
    }

    @Override
    public List<String> findDiseaseWithSuggestions(String diseaseName) {
        List<Map<String, Object>> drugs = diseaseRepository.findRelatedDrugs(diseaseName);
        List<Map<String, Object>> foods = diseaseRepository.findRelatedFoods(diseaseName);
        List<Map<String, Object>> recipes = diseaseRepository.findRelatedRecipes(diseaseName);
        List<String> allName = new ArrayList<>();
        for (Map<String, Object> drug : drugs) {
            String drugName = (String) drug.get("drugName");
            if (StringUtils.hasText(drugName)) {
                allName.add(drugName);
            }
        }
        for (Map<String, Object> food : foods) {
            String foodName = (String) food.get("foodName");
            if (StringUtils.hasText(foodName)) {
                allName.add(foodName);
            }
        }
        for (Map<String, Object> recipe : recipes) {
            String recipeName = (String) recipe.get("recipeName");
            if (StringUtils.hasText(recipeName)) {
                allName.add(recipeName);
            }
        }
        return allName;
    }
}
