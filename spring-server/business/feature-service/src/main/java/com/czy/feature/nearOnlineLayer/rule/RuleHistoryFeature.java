package com.czy.feature.nearOnlineLayer.rule;

import com.czy.api.domain.ao.feature.ScoreAo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/19 16:36
 */
@Slf4j
@Component
public class RuleHistoryFeature {

    private final double clickWeight = 0.005;
    private final float implicitWeight = 0.6f;
    private final float explicitWeight = 1.2f;

    public double execute(ScoreAo scoreAo) {
        if (scoreAo == null || scoreAo.isEmpty()){
            return 0.0;
        }
        return scoreAo.getClickTimes() * clickWeight +
                scoreAo.getImplicitScore() * implicitWeight +
                scoreAo.getExplicitScore() * explicitWeight;
    }


}
