package com.czy.api.domain.Do.neo4j.rels;

import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/19 14:31
 */
@Data
public class UserEntityRelation {

    private Long userId;

    private String entityName;

    // 添加实体类型字段，初始值为NULL
    private Integer entityType = DiseasesKnowledgeGraphEnum.NULL.getValue();

    private Integer clickTimes = 0;

    // 添加评分字段，初始值为0.0
    private Double implicitScore = 0.0;

    private Double explicitScore = 0.0;

    private Long lastUpdateTimestamp = System.currentTimeMillis();

}
