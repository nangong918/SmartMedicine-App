package com.czy.feature.nearOnlineLayer.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/15 17:03
 */
@Slf4j
@Component
public class RuleUserBrowseHeat {

    /**
     * 用户浏览 -> 活跃度
     * y = a * log30(b * x + c) + d
     * 经过点（0，0）（30，1）大致经过过（3600，10）
     * a = 25.0
     * b = 0.01237
     * c = 1.0
     * d = 0.0
     * @param browseTimes
     * @return
     */
    public double execute(Long browseTimes) {
        if (browseTimes == null || browseTimes <= 0) {
            return 0.0;
        }

        double seconds = browseTimes / 1000.0;

        // 解方程得到的参数
        double a = 12.0;
        double b = 12.37;
        double c = 1.0;
        double d = 0.0;

        // 计算以30为底的对数
        double log30 = Math.log(b * seconds + c) / Math.log(30);

        // 计算对数权重
        return a * log30 + d;
    }

    public static void main(String[] args) {
        RuleUserBrowseHeat ruleUserBrowseHeat = new RuleUserBrowseHeat();
        System.out.println("0s: " + ruleUserBrowseHeat.execute(0L));       // 应接近0
        System.out.println("30s: " + ruleUserBrowseHeat.execute(30L)); // 应接近1
        System.out.println("5分钟: " + ruleUserBrowseHeat.execute(5 * 60L));
        System.out.println("15分钟: " + ruleUserBrowseHeat.execute(15 * 60L));
        System.out.println("1小时: " + ruleUserBrowseHeat.execute(60 * 60L)); // 应接近10
    }




}
