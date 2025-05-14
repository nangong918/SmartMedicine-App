package com.czy.feature.rule;

import com.czy.api.domain.ao.feature.PostHeatAo;
import com.czy.api.domain.ao.feature.ScoreAo;
import com.czy.api.domain.ao.feature.ScoreDaysAo;
import json.BaseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 13225
 * @date 2025/5/14 9:53
 * 帖子热度规则
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RulePostHeat {

    private final RuleTempFeature ruleTempFeature;
    private final RuleClickWeight ruleClickWeight;

    /**
     * 帖子热度规则集
     * @param scoreDaysAos      帖子评分
     * @param postId            帖子id
     * @return                  帖子热度
     */
    public PostHeatAo execute(List<ScoreDaysAo> scoreDaysAos, Long postId){
        PostHeatAo postHeatAo = new PostHeatAo();
        postHeatAo.setPostId(postId);

        ScoreAo scoreAo = ruleTempFeature.execute(scoreDaysAos);
        double heat = calculatePostHeat(scoreAo);

        postHeatAo.setHeatScore(heat);
        return postHeatAo;
    }

    private static float implicitWeight = 0.6f;

    /**
     * 计算帖子热度
     * 显性特征为主，隐性特征为辅，点击为权重，热度无上限。
     * 如果显性特征是负值，则热度也是fuzhi
     * @param scoreAo   帖子的各项分数
     * @return
     */
    private double calculatePostHeat(ScoreAo scoreAo){
        if (scoreAo.isEmpty()){
            return 0.0;
        }
        double clickWeight = ruleClickWeight.execute(scoreAo.getClickTimes());
        return (
                scoreAo.getExplicitScore() + implicitWeight * scoreAo.getImplicitScore()
        ) * clickWeight;
    }


    //=============================Test=============================



    /**
     * 生成测试用的ScoreDaysAo列表
     * @param count 生成数量
     * @return 测试数据列表
     */
    private static List<ScoreDaysAo> generateScoreDaysAos(int count) {
        Random random = new Random();
        List<ScoreDaysAo> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ScoreDaysAo scoreDaysAo = new ScoreDaysAo();
            scoreDaysAo.setDays(random.nextInt(31)); // 0~30随机天数

            ScoreAo scoreAo = new ScoreAo();
            scoreAo.setClickTimes(random.nextInt(1000)); // 0~999随机点击
            scoreAo.setImplicitScore(random.nextDouble() * 20 - 10); // -10~10随机隐性评分
            scoreAo.setExplicitScore(random.nextDouble() * 20 - 10); // -10~10随机显性评分

            scoreDaysAo.setScoreAo(scoreAo);
            result.add(scoreDaysAo);
        }
        return result;
    }

    public static void main(String[] args) {
        testPostHeatCalculation();
    }

    private static class TempClass implements BaseBean {
        public ScoreAo scoreAo;
        public Double heat;
    }

    private static void testPostHeatCalculation() {
        RuleTempFeature ruleTempFeature = new RuleTempFeature();
        RuleClickWeight ruleClickWeight = new RuleClickWeight();
        RulePostHeat rulePostHeat = new RulePostHeat(
                ruleTempFeature,
                ruleClickWeight
        );

        Random random = new Random();

        List<TempClass> tempClasses = new ArrayList<>();
        for (long postId = 1; postId <= 100; postId++) {
            List<ScoreDaysAo> scoreDaysAos = generateScoreDaysAos(random.nextInt(5) + 1); // 每个帖子1~5个评分记录

            TempClass tempClass = new TempClass();
            tempClass.scoreAo = ruleTempFeature.execute(scoreDaysAos);
            PostHeatAo postHeatAo = rulePostHeat.execute(scoreDaysAos, postId);
            tempClass.heat = postHeatAo.getHeatScore();
            tempClasses.add(tempClass);
        }

        // 排序
        tempClasses.sort(
                (o1, o2) ->
                        o2.heat.compareTo(o1.heat)
        );

        for (TempClass tempClass : tempClasses) {
            System.out.println(tempClass.toJsonString());
        }
    }

}
