package com.czy.api.constant.feature;

import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import com.czy.api.mapper.UserFeatureRepository;
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

    public static String nodeLabelToRelation(String nodeLabel){
        if (StringUtils.hasText(nodeLabel)){
            switch (nodeLabel) {
                case ChecksDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_CHECKS;
                case DepartmentsDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_DEPARTMENTS;
                case DiseaseDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_DISEASES;
                case DrugsDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_DRUGS;
                case FoodsDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_FOODS;
                case ProducersDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_PRODUCERS;
                case RecipesDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_RECIPES;
                case SymptomsDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_SYMPTOMS;
                default:
                    return null;
            }
        }
        return null;
    }

}
