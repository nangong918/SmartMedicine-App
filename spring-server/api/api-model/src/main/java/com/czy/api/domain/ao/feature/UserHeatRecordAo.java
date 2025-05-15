package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/15 16:57
 */
@Data
public class UserHeatRecordAo {
    private Long userId;
    private Double heatScore = 0.0;
    private Long timestamp;
}
