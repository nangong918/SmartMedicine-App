package com.czy.feature.nearOnlineLayer.rule;

import com.czy.api.domain.ao.feature.HeatDaysAo;
import com.czy.api.domain.ao.feature.UserHeatAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/15 17:03
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RuleUserHeat {

    private final RuleTempFeature ruleTempFeature;

    public UserHeatAo execute(List<HeatDaysAo> heatDaysAos, Long userId) {
        UserHeatAo userHeatAo = new UserHeatAo();
        userHeatAo.setUserId(userId);

        userHeatAo.setHeatScore(ruleTempFeature.executeHeat(heatDaysAos));

        return userHeatAo;
    }

}
