package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.io.Serializable;

/**
 *@author 13225
 *@date 2025/5/15 18:28
 */
@Data
public class HeatDaysAo implements Serializable {
    private double score = 0.0;
    private int days = 30;
}
