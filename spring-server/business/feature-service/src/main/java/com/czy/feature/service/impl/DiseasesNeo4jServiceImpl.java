package com.czy.feature.service.impl;

import com.czy.api.api.feature.DiseasesNeo4jService;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.feature.mapper.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
            if (accompanyingDiseaseName != null) {
                accompanyingDiseaseNames.add(accompanyingDiseaseName);
            }
        }
        return accompanyingDiseaseNames;
    }
}
