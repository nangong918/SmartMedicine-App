package com.ai.medicine.service.mcp;

import com.ai.medicine.domain.IntentRecognitionResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/22 16:52
 */
public interface QuestionToolService {


    @Tool(name = "searchMedicalKnowledge", description = "根据识别到的医疗意图和实体，从知识库中查询相关信息, 返回查询的数据List")
    List<String> searchKnowledge(
            @ToolParam(description = "意图识别结果列表，包含用户问题的意图分类和提取的医疗实体", required = true)
            List<IntentRecognitionResult> results
    );
}
