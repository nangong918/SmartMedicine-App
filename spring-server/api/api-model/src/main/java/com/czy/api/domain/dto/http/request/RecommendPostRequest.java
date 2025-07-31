package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.ao.feature.FeatureContext;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/5/24 11:32
 */
@Data
public class RecommendPostRequest {
    @Valid
    @NotNull(message = "特征上下文不能为空")
    private FeatureContext featureContext;
}
