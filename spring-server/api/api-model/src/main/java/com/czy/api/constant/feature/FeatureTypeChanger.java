package com.czy.api.constant.feature;

import org.springframework.util.StringUtils;

/**
 * @author 13225
 * @date 2025/5/10 15:33
 */
public class FeatureTypeChanger {

    public static String nerTypeToEntityLabel(String nerType){
        if (StringUtils.hasText(nerType)){
            switch (nerType){
                case "checks":
                    return "检查";
                case "departments":
                    return "科室";
                case "diseases":
                    return "疾病";
                case "drugs":
                    return "药品";
                case "foods":
                    return "食物";
                case "producers":
                    return "药企";
                case "recipes":
                    return "菜谱";
                case "symptoms":
                    return "症状";
                default:
                    return null;
            }
        }
        return null;
    }

    public static String nerTypeToUserRelationType(String nerType){
        if (StringUtils.hasText(nerType)){
            return "user_" + nerType;
        }
        return null;
    }

}
