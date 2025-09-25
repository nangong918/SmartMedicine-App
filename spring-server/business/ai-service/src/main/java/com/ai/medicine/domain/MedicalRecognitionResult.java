package com.ai.medicine.domain;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;


/**
 * @author 13225
 * @date 2025/9/25 10:49
 */
@Data
public class MedicalRecognitionResult {

    /**
     * 识别出的意图类别，例如："伴随疾病"、"所属科目"、"治疗科室"、"应该吃"、"伴随症状"
     */
    @ToolParam(description = "识别出的关系信息")
    private Relation relation;
    /**
     * 从用户问题中提取的实体列表，包含相关的医疗术语和概念
     */
    @ToolParam(description = "从用户问题中提取的实体列表，包含相关的医疗术语和概念")
    private List<Entity> entities;


    /**
     * 关系模型，包含从文本中提取的医疗相关关系信息
     */
    @Data
    public static class Relation {
        /**
         * 实体的类型分类，例如："伴随疾病"、"所属科目"、"治疗科室"、"应该吃"、"伴随症状"
         * @see com.ai.medicine.domain.constant.MedicalRelationEnum
         */
        @ToolParam(description = "实体的类型分类, 参考: MedicalRelationEnum")
        private String type;
    }

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
         * 实体的类型分类，例如："检查"、"疾病"、"症状"、"科室"、"药企"
         * @see com.ai.medicine.domain.constant.MedicalEntityEnum
         */
        @ToolParam(description = "实体的类型分类, 参考: MedicalEntityEnum")
        private String type;
    }

}
