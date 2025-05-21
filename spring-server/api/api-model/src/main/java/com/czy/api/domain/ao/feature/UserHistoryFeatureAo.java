package com.czy.api.domain.ao.feature;

import com.czy.api.domain.Do.neo4j.rels.UserEntityRelation;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 13225
 * @date 2025/5/19 15:03
 */
@Data
public class UserHistoryFeatureAo {

    // 用户历史特征 (entity权重集合 -> 画像)
    private Set<UserEntityRelation> userEntityRelations = new HashSet<>();

}
