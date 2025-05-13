package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/12 18:06
 */
@Data
public class PostExplicitTimeAo {
    private Long userId;
    private List<PostExplicitPostScoreAo> postExplicitPostScoreAos;
//    private List<PostExplicitEntityScoreAo> postExplicitEntityScoreAos;
//    private List<PostExplicitLabelScoreAo> postExplicitLabelScoreAos;
    private Long timestamp;
}
