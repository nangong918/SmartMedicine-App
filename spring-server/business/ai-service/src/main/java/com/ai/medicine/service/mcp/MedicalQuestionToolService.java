package com.ai.medicine.service.mcp;

import com.ai.medicine.domain.MedicalRecognitionResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/25 12:00
 */
public interface MedicalQuestionToolService {
    @Tool(name = "searchKnowledge", description = "根据识别到的医疗关系和实体，从知识库中查询相关信息, 返回查询的知识List")
    List<String> searchKnowledge(
            @ToolParam(description = "医疗问题的关系和实体识别结果列表，包含用户问题内包含的关系和提取的医疗实体", required = true)
            List<MedicalRecognitionResult> results
    );

    @Tool(name = "entityKnowledge", description = "单个实体查询知识")
    List<String> entityKnowledge(
            @ToolParam(description = "单个实体", required = true)
            MedicalRecognitionResult.Entity entity
    );
}
