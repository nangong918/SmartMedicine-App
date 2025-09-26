package com.ai.medicine.domain.constant;

/**
 * @author 13225
 * @date 2025/9/22 11:40
 */
public enum IntentEnum {

    // 未知
    UNKNOWN(-1, "未知"),
    // 伴随疾病 acompany_with: 疾病 导致另一种 疾病
    ACCOMPANY_WITH(1, "伴随疾病"),
    // 所属科目 belongs_to: 科室 所属 科目
    BELONGS_TO(2, "所属科目"),
    // 治疗科室 cure_department: 疾病 治疗的对应 科室
    CURE_DEPARTMENT(3, "治疗科室"),
    // 应该吃 do_eat: 疾病 推荐吃 食物
    DO_EAT(4, "应该吃"),
//    // 治疗药物 has_common_drug: 疾病 应该吃 药品
//    HAS_COMMON_DRUG(5, "治疗药物"),
    // 伴随症状 has_symptom: 疾病 伴随 症状
    HAS_SYMPTOM(5, "伴随症状"),
    // 应该检查 need_check: 疾病 需要做 检查
    NEED_CHECK(6, "应该检查"),
    // 不该吃 not_eat: 疾病 不应该吃 食物
    NOT_EAT(7, "不该吃"),
    // 药企查询 production: 药品 对应 药企
    PRODUCTION(8, "药品企查询"),
    // 推荐药品 recommand_drug: 疾病 推荐吃 药品
    RECOMMEND_DRUG(9, "推荐药品"),



    // 其他 (Ai自行决定)
    OTHER(999, "其他")
    ;

    private final int code;
    private final String description;

    IntentEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static IntentEnum getByCode(int code) {
        for (IntentEnum value : IntentEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public static IntentEnum getByDescription(String description) {
        for (IntentEnum value : IntentEnum.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public static String getAiPrompt() {
        StringBuilder prompt = new StringBuilder("[");
        for (IntentEnum value : IntentEnum.values()) {
            prompt.append("\"").append(value.getDescription()).append("\",");
        }
        prompt.deleteCharAt(prompt.length() - 1);
        prompt.append("]");
        return prompt.toString();
    }

    public static void main(String[] args) {
        System.out.println(IntentEnum.getAiPrompt());
    }
}
