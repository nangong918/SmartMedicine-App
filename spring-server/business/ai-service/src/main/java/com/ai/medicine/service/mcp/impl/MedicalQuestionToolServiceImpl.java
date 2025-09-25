package com.ai.medicine.service.mcp.impl;

import com.ai.medicine.domain.Do.neo4j.DiseaseDo;
import com.ai.medicine.domain.Do.neo4j.base.BaseNeo4jMatchDo;
import com.ai.medicine.domain.MedicalRecognitionResult;
import com.ai.medicine.domain.constant.MedicalEntityEnum;
import com.ai.medicine.domain.constant.MedicalRelationEnum;
import com.ai.medicine.mapper.DiseaseRepository;
import com.ai.medicine.mapper.rels.DiseaseSymptomRelationRepository;
import com.ai.medicine.service.mcp.MedicalQuestionToolService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/9/25 12:55
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MedicalQuestionToolServiceImpl implements MedicalQuestionToolService {

    private final DiseaseRepository diseaseRepository;
    private final DiseaseSymptomRelationRepository diseaseSymptomRelationRepository;

    @Tool(name = "searchKnowledge", description = "根据识别到的医疗关系和实体，从知识库中查询相关信息, 返回查询的知识List")
    @Override
    public List<String> searchKnowledge(
            @ToolParam(description = "医疗问题的关系和实体识别结果列表，包含用户问题内包含的关系和提取的医疗实体", required = true)
            List<MedicalRecognitionResult> results
    ) {

        List<String> questionKnowledgeList = new ArrayList<>();

        if (CollectionUtils.isEmpty(results)){
            return new ArrayList<>();
        }

        for (MedicalRecognitionResult result : results){
            // 询问症状可能是哪些疾病？
            if (MedicalRelationEnum.HAS_SYMPTOM.getDescription().equals(result.getRelation().getType())){
                List<String> symptomNames = new ArrayList<>();
                for (MedicalRecognitionResult.Entity entity : result.getEntities()){
                    if (MedicalEntityEnum.SYMPTOM.getDescription().equals(entity.getType())){
                        symptomNames.add(entity.getName());
                    }
                }
                if (!symptomNames.isEmpty()){
                    List<BaseNeo4jMatchDo> diseaseMatchs = diseaseSymptomRelationRepository.findDiseaseMatchsBySymptomNames(symptomNames);

                    String matchDocument = getMatchDocument(diseaseMatchs, symptomNames);

                    questionKnowledgeList.add(matchDocument);
                }
            }
            // 其他查询
            for (MedicalRecognitionResult.Entity entity : result.getEntities()){
                if (MedicalEntityEnum.DISEASE.getDescription().equals(entity.getType())){
                    log.info("疾病名称: {}", entity.getName());
                    DiseaseDo diseaseDo = diseaseRepository.findByName(entity.getName());
                    String knowledge = diseaseDo.toDocumentString();
//                    log.info("知识: {}", knowledge);
                    questionKnowledgeList.add(knowledge);
                }
            }
        }

        log.info("数据检查: {}", questionKnowledgeList);

        return questionKnowledgeList;
    }

    private String getMatchDocument(List<BaseNeo4jMatchDo> diseaseMatchs, @NonNull List<String> symptomNames){
        String symptomNamesStr = symptomNames.stream().map(symptomName -> "\"" + symptomName + "\"").collect(Collectors.joining(","));
        if (CollectionUtils.isEmpty(diseaseMatchs)){
            return symptomNamesStr + "未匹配到相关疾病";
        }


        String matchDocument = diseaseMatchs.stream()
                .map(BaseNeo4jMatchDo::toDocumentString)
                .collect(Collectors.joining(","));

        return "根据症状" + symptomNamesStr + "匹配到的疾病有：" + matchDocument;
    }

    @Tool(name = "entityKnowledge", description = "单个实体查询知识")
    @Override
    public List<String> entityKnowledge(
            @ToolParam(description = "单个实体", required = true)
            MedicalRecognitionResult.Entity entity
    ){
        List<String> questionKnowledgeList = new ArrayList<>();
        if (entity.getType().equals(MedicalEntityEnum.DISEASE.getDescription())){
            DiseaseDo diseaseDo = diseaseRepository.findByName(entity.getName());
            questionKnowledgeList.add(diseaseDo.toDocumentString());
        }
        else {
            questionKnowledgeList.add("未匹配到相关疾病");
        }

        log.info("数据检查: {}", questionKnowledgeList);

        return questionKnowledgeList;
    }

}
