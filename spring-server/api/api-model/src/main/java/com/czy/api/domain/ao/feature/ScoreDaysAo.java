package com.czy.api.domain.ao.feature;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/5/13 15:44
 */
@Data
public class ScoreDaysAo implements Serializable {
    private ScoreAo scoreAo = new ScoreAo();
    private int days;
}
