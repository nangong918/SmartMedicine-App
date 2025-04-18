package com.czy.springUtils.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * @author 13225
 * @date 2024/12/23 14:16
 */
public class RandomDataGenerator {
    private static final Random random = new Random();

    // 随机生成一个汉字
    private static char getRandomChineseCharacter() {
        int high = 0x4E00; // 汉字起始值
        int low = 0x9FA5; // 汉字结束值
        int codePoint = random.nextInt(low - high + 1) + high;
        return (char) codePoint;
    }

    // 生成随机字符串
    public static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    // 生成随机中文名字
    public static String generateRandomChineseName() {
        StringBuilder name = new StringBuilder();
        int nameLength = new Random().nextInt(2) + 2; // 随机长度2到3个汉字
        for (int j = 0; j < nameLength; j++) {
            name.append(getRandomChineseCharacter());
        }
        return name.toString();
    }
}
