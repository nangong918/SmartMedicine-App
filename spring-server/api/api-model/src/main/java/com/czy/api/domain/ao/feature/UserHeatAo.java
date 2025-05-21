package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/15 18:13
 */
@Data
public class UserHeatAo {
    private Long userId;
    private Double heatScore = 0.0;
}
