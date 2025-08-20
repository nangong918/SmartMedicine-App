package com.czy.imports.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

/**
 * @author 13225
 * @date 2025/8/20 10:54
 */
public class RandomDateTimeGenerator {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++){
            LocalDateTime[] startAndEndDateTimes = getRandomStartAndEndDateTimes();
            System.out.println("开始时间：" + startAndEndDateTimes[0]);
            System.out.println("结束时间：" + startAndEndDateTimes[1]);
        }
    }

    // 生成随机时间(index必须对应)
    private static final LocalTime[] startTimes = new LocalTime[]{
        LocalTime.of(9, 0), LocalTime.of(14, 30)
    };
    private static final LocalTime[] endTimes = new LocalTime[]{
            LocalTime.of(11, 30), java.time.LocalTime.of(17, 0)
    };
    private static final int MAX_DAYS = 4;

    public static LocalDateTime[] getRandomStartAndEndDateTimes() {
        LocalDate nowDate = LocalDate.now();
        LocalDate randomDate = nowDate.plusDays(new Random().nextInt(MAX_DAYS - 1));
        int randomIndex = new Random().nextInt(startTimes.length);
        return new LocalDateTime[]{
                LocalDateTime.of(randomDate, startTimes[randomIndex]),
                LocalDateTime.of(randomDate, endTimes[randomIndex])
        };
    }
}
