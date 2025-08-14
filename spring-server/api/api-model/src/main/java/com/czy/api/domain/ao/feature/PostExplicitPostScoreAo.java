package com.czy.api.domain.ao.feature;


import lombok.Data;

import java.io.Serializable;

@Data
public class PostExplicitPostScoreAo implements Serializable {
    private Long postId;
    private Double score;
    private Long timestamp;
}
