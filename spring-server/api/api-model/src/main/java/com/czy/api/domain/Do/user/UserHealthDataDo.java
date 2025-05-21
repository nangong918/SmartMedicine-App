package com.czy.api.domain.Do.user;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 CREATE TABLE user_health_data (
 id BIGINT PRIMARY KEY,
 user_id BIGINT NOT NULL,
 time BIGINT NOT NULL,
 hypertension TINYINT(1),
 high_cholesterol TINYINT(1),
 bmi TINYINT(1),
 smoking TINYINT(1),
 stroke TINYINT(1),
 physical_activity TINYINT(1),
 fruit_consumption TINYINT(1),
 vegetable_consumption TINYINT(1),
 heavy_drinking TINYINT(1),
 any_healthcare TINYINT(1),
 no_medical_expense TINYINT(1),
 general_health_status TINYINT(1),
 mental_health TINYINT(1),
 physical_health TINYINT(1),
 walking_difficulty TINYINT(1),
 gender TINYINT(1),
 age INT,
 education_level TINYINT(1),
 income INT,
 INDEX (user_id)
 );

 */
@Data
public class UserHealthDataDo {
    @Id
    private Long id;
    private Long userId;
    private Long time; // 以 BIGINT 存储的时间戳
    private Integer hypertension; // 高血压
    private Integer highCholesterol; // 高胆固醇
    private Integer bmi; // 身体质量指数BMI
    private Integer smoking; // 吸烟
    private Integer stroke; // 中风
    private Integer physicalActivity; // 体力运动
    private Integer fruitConsumption; // 水果消费
    private Integer vegetableConsumption; // 蔬菜消费
    private Integer heavyDrinking; // 重度饮酒
    private Integer anyHealthcare; // 任何医疗保健
    private Integer noMedicalExpense; // 没有医疗花费
    private Integer generalHealthStatus; // 一般健康状况
    private Integer mentalHealth; // 心理健康
    private Integer physicalHealth; // 身体健康
    private Integer walkingDifficulty; // 行走困难
    private Integer gender; // 性别
    private Integer age; // 年龄
    private Integer educationLevel; // 教育水平
    private Integer income; // 收入
}
