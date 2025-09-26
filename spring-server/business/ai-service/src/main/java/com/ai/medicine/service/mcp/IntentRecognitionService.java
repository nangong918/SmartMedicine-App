package com.ai.medicine.service.mcp;

import com.ai.medicine.domain.IntentRecognitionResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/22 9:52
 */
public interface IntentRecognitionService {
    @Tool(description = "输出用户意图识别的结果")
    void recognizeOutput(
            @ToolParam(description = "问题的意图/实体识别结果list")
            List<IntentRecognitionResult> results
    );
}
