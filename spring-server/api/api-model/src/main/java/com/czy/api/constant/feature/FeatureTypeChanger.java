package com.czy.api.constant.feature;

import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.PostLabelNeo4jDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import com.czy.api.mapper.ChecksRepository;
import com.czy.api.mapper.DepartmentsRepository;
import com.czy.api.mapper.DrugsRepository;
import com.czy.api.mapper.FoodsRepository;
import com.czy.api.mapper.ProducersRepository;
import com.czy.api.mapper.RecipesRepository;
import com.czy.api.mapper.SymptomsRepository;
import com.czy.api.mapper.UserFeatureRepository;
import org.springframework.util.StringUtils;

/**
 * @author 13225
 * @date 2025/5/10 15:33
 */
public class FeatureTypeChanger {

    public static String[] nerTypes = new String[]{
            "checks",
            "departments",
            "diseases",
            "drugs",
            "foods",
            "producers",
            "recipes",
            "symptoms",
            "post_label"
    };

    public static String nerTypeToEntityLabel(String nerType){
        if (StringUtils.hasText(nerType)){
            switch (nerType){
                case "checks":
                    return ChecksDo.nodeLabel;
                case "departments":
                    return DepartmentsDo.nodeLabel;
                case "diseases":
                    return DiseaseDo.nodeLabel;
                case "drugs":
                    return DrugsDo.nodeLabel;
                case "foods":
                    return FoodsDo.nodeLabel;
                case "producers":
                    return ProducersDo.nodeLabel;
                case "recipes":
                    return RecipesDo.nodeLabel;
                case "symptoms":
                    return SymptomsDo.nodeLabel;
                case "post_label":
                    return PostLabelNeo4jDo.nodeLabel;
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
                case PostLabelNeo4jDo.nodeLabel:
                    return UserFeatureRepository.RELS_USER_POST_LABEL;
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     *             "checks",
     *             "departments",
     *             "diseases",
     *             "drugs",
     *             "foods",
     *             "producers",
     *             "recipes",
     *             "symptoms",
     *             "post_label"
     * @param nodeLabel
     * @return
     */
    public static String getRelationCQL(String nodeLabel){
        if (StringUtils.hasText(nodeLabel)){
            switch (nodeLabel) {
                case "checks":
                    return ChecksRepository.CQL_USER_CHECKS;
                case "departments":
                    return DepartmentsRepository.CQL_USER_DEPARTMENTS;
//                case "diseases":
//                    return DiseaseRepository.CQL_USER_DISEASES;
                case "drugs":
                    return DrugsRepository.CQL_USER_DRUGS;
                case "foods":
                    return FoodsRepository.CQL_USER_FOODS;
                case "producers":
                    return ProducersRepository.CQL_USER_PRODUCERS;
                case "recipes":
                    return RecipesRepository.CQL_USER_RECIPES;
                case "symptoms":
                    return SymptomsRepository.CQL_USER_SYMPTOMS;
            }
        }
        return null;
    }

}
