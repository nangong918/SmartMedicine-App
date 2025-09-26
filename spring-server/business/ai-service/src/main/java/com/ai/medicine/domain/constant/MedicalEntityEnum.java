package com.ai.medicine.domain.constant;

/**
 * @author 13225
 * @date 2025/9/22 11:46
 */
public enum MedicalEntityEnum {

    // 未知
    UNKNOWN(-1, "未知实体"),
    // 检查
    CHECK(1, "检查"),
    // 疾病
    DISEASE(2, "疾病"),
    // 症状
    SYMPTOM(3, "症状"),
    // 科室
    DEPARTMENT(4, "科室"),
    // 药企
    COMPANY(5, "药企"),
    // 药品
    DRUGS(6, "药品"),
    // 医院
    HOSPITAL(7, "医院"),
    // 食物
    FOOD(8, "食物"),
    // 其他
    OTHER(999, "其他"),

    ;
    private final int code;
    private final String description;

    MedicalEntityEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MedicalEntityEnum getByCode(int code) {
        for (MedicalEntityEnum value : MedicalEntityEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public static MedicalEntityEnum getByDescription(String description) {
        for (MedicalEntityEnum value : MedicalEntityEnum.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public static String getAiPrompt() {
        StringBuilder prompt = new StringBuilder("[");
        for (MedicalEntityEnum value : MedicalEntityEnum.values()) {
            prompt.append("\"").append(value.getDescription()).append("\",");
        }
        prompt.deleteCharAt(prompt.length() - 1);
        prompt.append("]");
        return prompt.toString();
    }

    public static void main(String[] args) {
        System.out.println(getAiPrompt());
    }
}
