package com.czy.api.domain.ao.feature;


import com.czy.api.constant.feature.PostTypeEnum;
import lombok.Data;

@Data
public class PostExplicitLabelScoreAo {
    private Integer label = PostTypeEnum.OTHER.getCode();
    private Double score;
    private Long timestamp;
}
