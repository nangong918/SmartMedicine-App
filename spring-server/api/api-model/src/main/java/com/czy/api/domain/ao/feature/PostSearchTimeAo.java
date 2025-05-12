package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/12 18:06
 */
@Data
public class PostSearchTimeAo {
    private Long userId;
    private List<PostSearchScoreAo> postSearchScoreAos;
    private List<PostSearchEntityScoreAo> postSearchEntityScoreAos;
    private Long searchTime;
}
