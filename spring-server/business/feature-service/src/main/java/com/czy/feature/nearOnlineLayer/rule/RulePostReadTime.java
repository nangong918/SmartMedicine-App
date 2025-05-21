package com.czy.feature.nearOnlineLayer.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/10 16:20
 * 帮我设计一个函数，用于判断用户读取时长和对文章感兴趣的程度。
 * y是感兴趣程度，值域是[-5.0,5.0]
 * 然后现在认定一般人一秒能读取6~15个字。
 * 代码入参是public double execute(Integer wordCount, Long readTime){}
 * 函数的x是阅读速度，阅读速度越快，则越不敢兴趣，阅读速度越慢则越感兴趣。
 * 不是一次函数，因为速度无线趋近于0的时候代表感兴趣，就无限趋近于5.0，
 * 如果速度无限大，则代表刚读就退出了，所以要无限趋近于-5.0，正常范围，
 * 其中函数中的关键点：一秒读取15个字的兴趣程度是-1,一秒读取10个字的速度是1
 */
@Slf4j
@Component
public class RulePostReadTime {

    public double execute(Integer wordCount, Long readTime) {
        // 非合法数据
        if (readTime <= 0) {
            return -5.0;
        }
        if (wordCount <= 0){
            return 5.0;
        }

        // 时间小于1秒直接判定不感兴趣
        if (readTime < 1000) {
            return -5.0;
        }

        // 计算阅读速度（字/秒）
        double seconds = readTime / 1000.0;
        double readingSpeed = wordCount / seconds;

        // 根据阅读速度计算兴趣程度
        // 核心函数 -5sigmoid + c
        double readSpeedScore = calculateInterest(readingSpeed);

        double readTimeScore = calculateBonus(readTime);

        return readSpeedScore + readTimeScore;
    }

    /**
     * 函数选型：-5sigmoid + c函数；大致经过 (1,5)且y=0的时候x大致等于11
     * @param speed     阅读速度（字/秒）
     * @return          兴趣程度（范围在[-5.0, 5.0]之间）
     */
    private double calculateInterest(double speed) {
        // 调整后的Sigmoid参数
        double k = 0.5;    // 调整曲线陡峭度
        double x0 = 12.5;  // 曲线中心点
        double a = 8.0;   // 振幅调整
        double c = -0.5;

        // 双Sigmoid变换函数
        double sigmoid = 1 / (1 + Math.exp(-k * (speed - x0)));
        double interest = a * (0.5 - sigmoid) + c; // 将输出范围调整为[-5,5]

        // 微调曲线通过关键点
        double adjustment = 3.5 * Math.exp(-1.2 * speed); // 对低速阅读的额外奖励
        interest += adjustment;

        // 确保结果在[-5,5]范围内
        return Math.max(-5.0, Math.min(5.0, interest));
    }

    /**
     * 帮我用log函数写一个长阅读加分函数：
     * 就比如当阅读时间超过20秒就开始用log函数加分，1分钟的时候大概加1分，最多加2分
     * score = min(2,k*log(t - 20))
     */
    private double calculateBonus(double time) {
        double k = 0.26; // 调整因子，确保1分钟加1分
        double maxBonus = 2.0; // 最大加分

        if (time <= 20) {
            return 0;
        } else {
            double bonus = k * Math.log(time - 20); // 使用log函数计算加分
            return Math.min(maxBonus, bonus); // 确保不超过最大加分
        }
    }


    public static void main(String[] args) {
        allScore();
    }

    private static void allScore(){
        RulePostReadTime calculator = new RulePostReadTime();
        int wordCount = 100;
        for (int t = 0; t < 60; t++) {
            double speed = (double) wordCount / t;
            double interest = calculator.calculateInterest(speed);
            double bonus = calculator.calculateBonus(t);
            double allScore = interest + bonus;
            System.out.println("time：" + t + "s，interest：" + "，allScore：" + allScore);
        }
    }

    private static void testCalculateBonus(){
        RulePostReadTime calculator = new RulePostReadTime();
        for (double time = 15; time <= 60; time++) {
            System.out.println("time：" + time + "s，score：" + calculator.calculateBonus(time));
        }
        /**
         * time：15.0s，score：0.0
         * time：16.0s，score：0.0
         * time：17.0s，score：0.0
         * time：18.0s，score：0.0
         * time：19.0s，score：0.0
         * time：20.0s，score：0.0
         * time：21.0s，score：0.0
         * time：22.0s，score：0.1802182669455858
         * time：23.0s，score：0.28563919505370855
         * time：24.0s，score：0.3604365338911716
         * time：25.0s，score：0.4184538572328661
         * time：26.0s，score：0.4658574619992943
         * time：27.0s，score：0.5059366387543814
         * time：28.0s，score：0.5406548008367573
         * time：29.0s，score：0.5712783901074171
         * time：30.0s，score：0.598672124178452
         * time：31.0s，score：0.6234527709275764
         * time：32.0s，score：0.6460757289448801
         * time：33.0s，score：0.6668868329399996
         * time：34.0s，score：0.6861549056999672
         * time：35.0s，score：0.7040930522865746
         * time：36.0s，score：0.7208730677823432
         * time：37.0s，score：0.7366354694546162
         * time：38.0s，score：0.7514966570530028
         * time：39.0s，score：0.7655541345832745
         * time：40.0s，score：0.7788903911240377
         * time：41.0s，score：0.79157583380809
         * time：42.0s，score：0.8036710378731622
         * time：43.0s，score：0.815228496141579
         * time：44.0s，score：0.8262939958904659
         * time：45.0s，score：0.8369077144657322
         * time：46.0s，score：0.8471050998855854
         * time：47.0s，score：0.8569175851611256
         * time：48.0s，score：0.866373172645553
         * time：49.0s，score：0.8754969157964833
         * time：50.0s，score：0.8843113192321604
         * time：51.0s，score：0.892836673166138
         * time：52.0s，score：0.9010913347279289
         * time：53.0s，score：0.9090919659812849
         * time：54.0s，score：0.9168537364002021
         * time：55.0s，score：0.9243904959872475
         * time：56.0s，score：0.9317149239985886
         * time：57.0s，score：0.9388386572874984
         * time：58.0s，score：0.9457724015288603
         * time：59.0s，score：0.9525260279937081
         * time：60.0s，score：0.9591086580696234
         */
    }

    private static void testCalculateInterest(){
        RulePostReadTime calculator = new RulePostReadTime();
        for (int speed = 1; speed <= 20; speed++){
            System.out.println("speed：" + speed + "，score：" + calculator.calculateInterest(speed));
        }
        /**
         *speed：1，score：4.528798278952826
         * speed：2，score：3.7757518309644764
         * speed：3，score：3.527013145255828
         * speed：4，score：3.416295098325606
         * speed：5，score：3.324856673338127
         * speed：6，score：3.2039979515762824
         * speed：7，score：3.020093834242565
         * speed：8，score：2.737441331384842
         * speed：9，score：2.3156938140084233
         * speed：10，score：1.718420394140766
         * speed：11，score：0.9334360705073353
         * speed：12，score：-0.002586042047323001
         * speed：13，score：-0.997411419496749
         * speed：14，score：-1.9334294164245467
         * speed：15，score：-2.7183988360925997
         * speed：16，score：-3.3156223996913483
         * speed：17，score：-3.7372042759714104
         * speed：18，score：-4.01930679715145
         * speed：19，score：-4.201384900808279
         * speed：20，score：-4.316181040587665
         */
    }

}
