package com.ai.medicine.service.mcp.impl;

import com.ai.medicine.domain.Do.neo4j.DiseaseDo;
import com.ai.medicine.domain.IntentRecognitionResult;
import com.ai.medicine.domain.constant.MedicalEntityEnum;
import com.ai.medicine.mapper.DiseaseRepository;
import com.ai.medicine.service.mcp.QuestionToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/9/22 16:53
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionToolServiceImpl implements QuestionToolService {

    private final DiseaseRepository diseaseRepository;

    @Tool(name = "searchMedicalKnowledge", description = "根据识别到的医疗意图和实体，从知识库中查询相关信息, 返回查询的数据List")
    @Override
    public List<String> searchKnowledge(
            @ToolParam(description = "意图识别结果列表，包含用户问题的意图分类和提取的医疗实体", required = true)
            List<IntentRecognitionResult> results
    ) {

        log.info("入参检查: {}", results);

        List<String> questionKnowledgeList = new ArrayList<>();
        if (CollectionUtils.isEmpty(results)){
            return new ArrayList<>();
        }
        for (IntentRecognitionResult result : results){
            for (IntentRecognitionResult.Entity entity : result.getEntities()){
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

}
