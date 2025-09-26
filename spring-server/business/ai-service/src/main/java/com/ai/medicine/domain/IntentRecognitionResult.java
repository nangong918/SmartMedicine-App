package com.ai.medicine.domain;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * 用户意图识别的结果模型
 */
@Data
public class IntentRecognitionResult {
    /**
     * 识别出的意图类别，例如："治疗方法"、"病因咨询"、"症状查询"、"药品信息"等
     */
    @ToolParam(description = "识别出的意图类别")
    private String intent;

    /**
     * 从用户问题中提取的实体列表，包含相关的医疗术语和概念
     */
    @ToolParam(description = "从用户问题中提取的实体列表，包含相关的医疗术语和概念")
    private List<Entity> entities;

    /**
     * 实体模型，包含从文本中提取的医疗相关实体信息
     */
    @Data
    public static class Entity {
        /**
         * 实体的具体名称，例如："失眠"、"高血压"、"阿司匹林"
         */
        @ToolParam(description = "实体的具体名称，例如：失眠、高血压、阿司匹林")
        private String name;

        /**
         * 实体的类型分类，例如："症状"、"疾病"、"药品"、"检查项目"、"治疗方法"
         */
        @ToolParam(description = "实体的类型分类")
        private String type;
    }
}
