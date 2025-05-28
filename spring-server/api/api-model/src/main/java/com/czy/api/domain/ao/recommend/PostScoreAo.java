package com.czy.api.domain.ao.recommend;

import lombok.Data;

/**
 *@author 13225
 *@date 2025/5/20 13:50
 */
@Data
public class PostScoreAo {
    private Long postId;
    private Double score = 0.0;

    public PostScoreAo() {
    }

    public PostScoreAo(Long postId, Double score) {
        this.postId = postId;
        this.score = score;
    }
}
