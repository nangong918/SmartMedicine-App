package com.czy.feature.rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/14 10:46
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RuleClickWeight {


    /**
     * 点击权重:log函数实现点击权重，0的时候是0.0，100点击的时候是1.0，500点击的时候是2.0解方程求出log函数
     * y = a * log10(b * x + c) + d
     * a ≈ 1.430676558
     * b = 1
     * c = 25
     * d = -2.0
     * @param clickTimes    点击次数
     * @return      点击权重
     */
    public double execute(Integer clickTimes) {
        if (clickTimes == null || clickTimes <= 0) {
            return 0.0;
        }

        // 解方程得到的参数
        double a = 1.0 / (Math.log10(125) - Math.log10(25));  // ≈ 1.430676558
        double c = 25.0;
        double d = -a * Math.log10(c);  // ≈ -2.0

        // 计算对数权重
        return a * Math.log10(clickTimes + c) + d;
    }

    public static void main(String[] args) {
        RuleClickWeight ruleClickWeight = new RuleClickWeight();
        for (int i = 0; i <= 1000; i+=100){
            System.out.println("点击次数：" + i + "，权重：" + ruleClickWeight.execute(i));
        }
    }

}
