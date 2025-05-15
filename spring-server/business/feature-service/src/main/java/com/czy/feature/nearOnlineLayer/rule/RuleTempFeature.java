package com.czy.feature.nearOnlineLayer.rule;

import com.czy.api.domain.ao.feature.HeatDaysAo;
import com.czy.api.domain.ao.feature.ScoreAo;
import com.czy.api.domain.ao.feature.ScoreDaysAo;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/13 15:38
 */
@Slf4j
@Component
public class RuleTempFeature {


    private static float calculateWeight(int days) {
        // 最大天数
        final int maxDays = 30;

        // 权重从1线性衰减到接近0（但不完全为0）
        if (days < 0) {
            return 1.0f; // 返回1.0表示未开始
        } else if (days >= maxDays) {
            return 0.01f; // 第30天返回接近0的值
        } else {
            return 1.0f - (float) days / maxDays * 0.99f; // 线性衰减
        }
    }

    public ScoreAo execute(List<ScoreDaysAo> scoreDaysAos) {
        ScoreAo scoreAo = new ScoreAo();
        if (Collections.isEmpty(scoreDaysAos)){
            return scoreAo;
        }
        for (ScoreDaysAo scoreDaysAo : scoreDaysAos){
            if (scoreDaysAo == null || scoreDaysAo.getScoreAo() == null || scoreDaysAo.getScoreAo().isEmpty()){
                continue;
            }
            float weight = calculateWeight(scoreDaysAo.getDays());
            ScoreAo newScoreAo = scoreDaysAo.getScoreAo().productBack(weight);
            scoreAo.add(newScoreAo);
        }
        return scoreAo;
    }

    public double execute2(List<HeatDaysAo> scores){
        double sum = 0.0;
        if (CollectionUtils.isEmpty(scores)){
            return sum;
        }
        for (HeatDaysAo score : scores){
            if (score == null){
                continue;
            }
            float weight = calculateWeight(score.getDays());
            sum += score.getScore() * weight;
        }
        return sum;
    }

    public static void main(String[] args) {
        for (int days = 0; days <= 30; days++) {
            float weight = calculateWeight(days);
            System.out.println("Days: " + days + ", Weight: " + weight);
        }
    }
}
