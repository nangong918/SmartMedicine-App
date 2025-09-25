package com.ai.medicine.service.mcp.impl;

import com.ai.medicine.domain.IntentRecognitionResult;
import com.ai.medicine.service.mcp.IntentRecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author 13225
 * @date 2025/9/22 9:52
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class IntentRecognitionServiceImpl implements IntentRecognitionService {

    @Tool(description = "输出用户意图识别的结果")
    @Override
    public void recognizeOutput(
            @ToolParam(description = "问题的意图/实体识别结果list")
            List<IntentRecognitionResult> results
    ) {
        for (IntentRecognitionResult result : results) {
            log.info("识别结果：{}", result);
        }
    }

}
