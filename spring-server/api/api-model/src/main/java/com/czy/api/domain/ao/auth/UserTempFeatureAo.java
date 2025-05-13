package com.czy.api.domain.ao.auth;

import com.czy.api.domain.ao.feature.PostExplicitEntityScoreAo;
import com.czy.api.domain.ao.feature.PostExplicitLabelScoreAo;
import com.czy.api.domain.ao.feature.PostExplicitPostScoreAo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/13 11:48
 */
@Data
public class UserTempFeatureAo {
    private List<PostExplicitPostScoreAo> postExplicitPostScoreAos;
    private List<PostExplicitEntityScoreAo> postExplicitEntityScoreAos;
    private List<PostExplicitLabelScoreAo> postExplicitLabelScoreAos;
}
