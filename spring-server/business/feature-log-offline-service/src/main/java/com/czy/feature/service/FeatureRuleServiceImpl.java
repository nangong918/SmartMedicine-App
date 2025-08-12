package com.czy.feature.service;

import com.czy.api.api.feature.FeatureRuleService;
import com.czy.api.domain.ao.feature.ScoreAo;
import com.czy.feature.rule.RuleHistoryFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/5/20 15:58
 */

@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class FeatureRuleServiceImpl implements FeatureRuleService {

    private final RuleHistoryFeature ruleHistoryFeature;

    @Override
    public Double scoreAoToScore(ScoreAo scoreAo) {
        return ruleHistoryFeature.execute(scoreAo);
    }
}
