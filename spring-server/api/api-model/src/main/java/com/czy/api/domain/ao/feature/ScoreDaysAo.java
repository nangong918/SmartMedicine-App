package com.czy.api.domain.ao.feature;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/13 15:44
 */
@Data
public class ScoreDaysAo {
    private ScoreAo scoreAo = new ScoreAo();
    private int days;
}
