package com.czy.api.domain.ao.feature;

import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/19 16:41
 */
@Data
public class UserEntityScore {

    private Long userId;
    private Double score = 0.0;
    private String entityName;
    private Integer entityType = DiseasesKnowledgeGraphEnum.NULL.getValue();

}
