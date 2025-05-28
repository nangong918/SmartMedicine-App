package com.czy.api.domain.dto.python;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/14 16:15
 */
@Data
public class MedicalPredictionResponse {
    private Integer code;
    private String message;
    private Double heartDisease;
    private Double diabetes;
}
